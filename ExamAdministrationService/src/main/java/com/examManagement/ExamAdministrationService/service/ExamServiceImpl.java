package com.examManagement.ExamAdministrationService.service;

import com.examManagement.ExamAdministrationService.Mapper.ExamMapper;
import com.examManagement.ExamAdministrationService.dto.ExamRequest;
import com.examManagement.ExamAdministrationService.dto.ExamResponse;
import com.examManagement.ExamAdministrationService.entity.Exam;
import com.examManagement.ExamAdministrationService.entity.ExamStatus;
import com.examManagement.ExamAdministrationService.exception.ResourceNotFoundException;
import com.examManagement.ExamAdministrationService.repository.ExamRepository;
import com.examManagement.ExamAdministrationService.repository.ExaminerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamServiceImpl implements ExamService{

    private final ExamRepository examRepository;
    private final ExaminerService examinerService;
    private final ExaminerRepository examinerRepository;

    @Override
    public ExamResponse createExam(ExamRequest request) {
        examinerService.validateExaminerIds(request.getAssignedExaminerId());
        Exam createdExam = ExamMapper.toEntity(request);
        examRepository.save(createdExam);
        return ExamMapper.toResponse(createdExam);
    }

    @Override
    public List<ExamResponse> getAllExams() {
        return examRepository.findAll().stream()
                .map(ExamMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExamResponse getExamById(String id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        return ExamMapper.toResponse(exam);
    }

    @Override
    public ExamResponse updateExam(String id, ExamRequest request) {
        Exam updatedExam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        examinerService.validateExaminerIds(request.getAssignedExaminerId());
        updatedExam.setDescription(request.getDescription());
        updatedExam.setScheduledTime(request.getScheduledTime());
        updatedExam.setName(request.getName());
        updatedExam.setDuration(request.getDuration());
        updatedExam.setAssignedExaminerId(request.getAssignedExaminerId());
        examRepository.save(updatedExam);
        return ExamMapper.toResponse(updatedExam);
    }

    @Override
    public void deleteExam(String id) {
        Exam deletedExam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        examRepository.delete(deletedExam);
    }

    @Override
    public List<ExamResponse> getExamsByExaminerId(String examinerId) {
        return examRepository.findByAssignedExaminerId(examinerId).stream()
                .map(ExamMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExamResponse addExaminerToExam(String examId, String examinerId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with ID: " + examId));

        if (!examinerRepository.existsById(examinerId)) {
            throw new ResourceNotFoundException("Examiner not found with ID: " + examinerId);
        }

        // Check if examiner is already assigned
        if (exam.getAssignedExaminerId() != null &&
                exam.getAssignedExaminerId().contains(examinerId)) {
            throw new IllegalArgumentException("Examiner is already assigned to this exam");
        }

        if (exam.getAssignedExaminerId() == null) {
            exam.setAssignedExaminerId(new ArrayList<>());
        }
        exam.getAssignedExaminerId().add(examinerId);

        Exam updatedExam = examRepository.save(exam);
        return ExamMapper.toResponse(updatedExam);
    }

    @Override
    public ExamResponse removeExaminerFromExam(String examId, String examinerId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with ID: " + examId));

        // Check if examiner is assigned
        if (exam.getAssignedExaminerId() == null ||
                !exam.getAssignedExaminerId().contains(examinerId)) {
            throw new IllegalArgumentException("Examiner is not assigned to this exam");
        }

        exam.getAssignedExaminerId().remove(examinerId);
        Exam updatedExam = examRepository.save(exam);
        return ExamMapper.toResponse(updatedExam);
    }

    @Override
    public ExamResponse publishExam(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found: "+examId));

        if (exam.getStatus() != ExamStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT exams can be published");
        }

        exam.setStatus(ExamStatus.PUBLISHED);
        examRepository.save(exam);
        return ExamMapper.toResponse(exam);
    }

    @Override
    public ExamResponse completeExam(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new IllegalStateException("Only PUBLISHED exams can be completed");
        }

        exam.setStatus(ExamStatus.COMPLETED);
        examRepository.save(exam);
        return ExamMapper.toResponse(exam);
    }

    @Override
    public ExamResponse cancelExam(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new IllegalStateException("Only PUBLISHED exams can be canceled");
        }

        exam.setStatus(ExamStatus.CANCELED);
        examRepository.save(exam);
        return ExamMapper.toResponse(exam);
    }

    @Override
    public ExamResponse revertToDraft(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        exam.setStatus(ExamStatus.DRAFT);
        examRepository.save(exam);
        return ExamMapper.toResponse(exam);
    }

    @Override
        public List<ExamResponse> batchPublishExams(List<String> examIds) {
            List<Exam> exams = examRepository.findAllById(examIds);

            if (exams.size() != examIds.size()) {
                List<String> foundIds = exams.stream().map(Exam::getId).toList();
                List<String> missingIds = examIds.stream()
                        .filter(id -> !foundIds.contains(id))
                        .toList();
                throw new ResourceNotFoundException("Missing exams: " + missingIds);
            }

            exams.forEach(exam -> {
                if (exam.getStatus() != ExamStatus.DRAFT) {
                    throw new IllegalStateException(
                            "Exam " + exam.getId() + " is not in DRAFT state"
                    );
                }
            });

            exams.forEach(exam -> exam.setStatus(ExamStatus.PUBLISHED));
            examRepository.saveAll(exams);

            return exams.stream()
                    .map(ExamMapper::toResponse)
                    .toList();
        }

}
