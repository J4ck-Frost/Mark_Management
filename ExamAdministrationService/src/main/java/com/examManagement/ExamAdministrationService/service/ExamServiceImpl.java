package com.examManagement.ExamAdministrationService.service;

import com.examManagement.ExamAdministrationService.Mapper.ExamMapper;
import com.examManagement.ExamAdministrationService.dto.ExamRequest;
import com.examManagement.ExamAdministrationService.dto.ExamResponse;
import com.examManagement.ExamAdministrationService.entity.Exam;
import com.examManagement.ExamAdministrationService.entity.ExamStatus;
import com.examManagement.ExamAdministrationService.exception.ResourceNotFoundException;
import com.examManagement.ExamAdministrationService.repository.ExamRepository;
import com.examManagement.ExamAdministrationService.repository.ExaminerRepository;
import com.examManagement.ExamAdministrationService.validation.ExamValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamServiceImpl implements ExamService{

    private final ExamRepository examRepository;
    private final ExaminerService examinerService;
    private final ExaminerRepository examinerRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ExamValidator examValidator;
    private final ExamMapper examMapper;

    @Override
    public ExamResponse createExam(ExamRequest request) {
        examinerService.validateExaminerIds(request.getAssignedExaminerId());
        Exam createdExam = examMapper.toEntity(request);
        examRepository.save(createdExam);
        return examMapper.toResponse(createdExam);
    }

    @Override
    public List<ExamResponse> getAllExams() {
        return examRepository.findAll().stream()
                .map(examMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "exam-info", key = "#id",
            unless = "#result == null")
    @Override
    public ExamResponse getExamById(String id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        System.out.println("👉 GỌI EXAM SERVICE (CACHE): " + id);
        return examMapper.toResponse(exam);
    }

    @CachePut(value = "exam-info", key = "#id",
            unless = "#result == null")
    @Override
    public ExamResponse updateExam(String id, ExamRequest request) {
        Exam updatedExam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        examinerService.validateExaminerIds(request.getAssignedExaminerId());
        updatedExam.setDescription(request.getDescription());
        updatedExam.setScheduledTime(request.getScheduledTime());
        updatedExam.setName(request.getName());
        updatedExam.setDuration(request.getDuration());
        updateAssignedExaminer(updatedExam, request.getAssignedExaminerId());
        examRepository.save(updatedExam);
        return examMapper.toResponse(updatedExam);
    }

    @CachePut(value = "exam-info", key = "#exam.id",
            unless = "#result == null")
    private void updateAssignedExaminer(Exam exam, List<String> examinerIds){
        for (String examinerId: examinerIds) {
            if (!examinerRepository.existsById(examinerId)) {
                throw new ResourceNotFoundException("Examiner not found with ID: " + examinerId);
            }
            if (!examinerRepository.existsByIdAndActiveTrue(examinerId)){
                throw new IllegalStateException("Cannot assign inactive examiner");
            }
        }
        if (!new HashSet<>(examinerIds).equals(new HashSet<>(exam.getAssignedExaminerId()))) {
            exam.setAssignedExaminerId(new ArrayList<>(examinerIds));
        }
    }

    @Override
    public void deleteExam(String id) {
        Exam deletedExam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        if (deletedExam.getStatus() == ExamStatus.PUBLISHED)
            throw new IllegalStateException("Cannot delete published exams can be deleted");
        examRepository.delete(deletedExam);
    }

    @Override
    public List<ExamResponse> getExamsByExaminerId(String examinerId) {
        return examRepository.findByAssignedExaminerId(examinerId).stream()
                .map(examMapper::toResponse)
                .collect(Collectors.toList());
    }

    @CachePut(value = "exam-info", key = "#id",
            unless = "#result == null")
    @Override
    public ExamResponse publishExam(String id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found: "+id));

        if (exam.getStatus() != ExamStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT exams can be published");
        }

        exam.setStatus(ExamStatus.PUBLISHED);
        examRepository.save(exam);
        return examMapper.toResponse(exam);
    }

    @CachePut(value = "exam-info", key = "#id",
            unless = "#result == null")
    @Override
    public ExamResponse scoreExam(String id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        examValidator.validateExamStatusForScoring(exam);

        exam.setStatus(ExamStatus.SCORED);
        examRepository.save(exam);
        return examMapper.toResponse(exam);
    }

    @CachePut(value = "exam-info", key = "#id",
            unless = "#result == null")
    @Override
    public ExamResponse completeExam(String id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        examValidator.validateExamStatusForCompletion(exam);

        sendKafkaEvent(id, "exam-complete-events");
        exam.setStatus(ExamStatus.COMPLETED);
        examRepository.save(exam);
        return examMapper.toResponse(exam);
    }

    @CachePut(value = "exam-info", key = "#id",
            unless = "#result == null")
    @Override
    public ExamResponse cancelExam(String id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        examValidator.validateExamStatusForCancellation(exam);

        exam.setStatus(ExamStatus.CANCELED);
        examRepository.save(exam);
        return examMapper.toResponse(exam);
    }

    @CachePut(value = "exam-info", key = "#id",
            unless = "#result == null")
    @Override
    public ExamResponse revertToDraft(String id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        exam.setStatus(ExamStatus.DRAFT);
        examRepository.save(exam);
        return examMapper.toResponse(exam);
    }

    @Override
        public List<ExamResponse> batchPublishExams(List<String> examIds) {
            List<Exam> exams = examRepository.findAllById(examIds);

            examValidator.validateExamsForBatchPublish(exams, examIds);

            exams.forEach(exam -> exam.setStatus(ExamStatus.PUBLISHED));
            examRepository.saveAll(exams);

            return exams.stream()
                    .map(examMapper::toResponse)
                    .toList();
        }

    private void sendKafkaEvent(String examId, String topic) {
        try {
            kafkaTemplate.send(topic, examId);
            System.out.println("✅ Đã gửi message vào Kafka topic.");
        } catch (Exception ex) {
            System.err.println("❌ Lỗi khi gửi Kafka: " + ex.getMessage());
        }
    }
}
