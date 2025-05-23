package com.examManagement.MarkManagementService.service;

import com.examManagement.MarkManagementService.client.ExamServiceClient;
import com.examManagement.MarkManagementService.dto.ExamResponse;
import com.examManagement.MarkManagementService.dto.MarkRequest;
import com.examManagement.MarkManagementService.dto.MarkResponse;
import com.examManagement.MarkManagementService.entity.Mark;
import com.examManagement.MarkManagementService.exception.ResourceNotFoundException;
import com.examManagement.MarkManagementService.mapper.MarkMapper;
import com.examManagement.MarkManagementService.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkServiceImpl implements MarkService{
    private final MarkRepository markRepository;
    private final ExamServiceClient examServiceClient;

    @KafkaListener(
            topics = "exam-registration-events",
            groupId = "mark-service-group"
    )
    @Override
    public void registerMark(String message) {
        System.out.println("Nhận event: " + message); // Log để debug

        String[] parts = message.split(":");
        if (parts.length != 2) {
            System.err.println("Message không hợp lệ: " + message);
            return;
        }

        String candidateId = parts[0];
        String examId = parts[1];
        Mark mark= new Mark();
        mark.setCandidateId(candidateId);
        mark.setExamId(examId);
        mark.setRegisteredAt(LocalDateTime.now());
        markRepository.save(mark);
        System.out.println("Đã tạo đăng ký kì thi thành công");
    }

    @Override
    public List<MarkResponse> findAllMark() {
        return markRepository.findAll().stream()
                .map(MarkMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MarkResponse findMarkById(String id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Mark not found"));
        return MarkMapper.toResponse(mark);
    }

    @Override
    public List<MarkResponse> findMarkByExamId(String examId) {
        return markRepository.findMarkByExamId(examId).stream()
                .map(MarkMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkResponse> findMarkByExaminerId(String examinerId) {
        return markRepository.findMarkByExaminerId(examinerId).stream()
                .map(MarkMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkResponse> findMarkByCandidateId(String candidateId) {
        return markRepository.findMarkByCandidateId(candidateId).stream()
                .map(MarkMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MarkResponse findMarkByCandidateIdAndExamId(String candidateId, String examId) {
        Mark mark = markRepository.findMarkByCandidateIdAndExamId(candidateId, examId)
                .orElseThrow(()-> new ResourceNotFoundException("Mark not found"));
        return MarkMapper.toResponse(mark);
    }

    @Override
    public MarkResponse updateMark(String id, MarkRequest request) {
        Mark updatedMark = markRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Mark not found"));
        ExamResponse exam = examServiceClient.getExamById(updatedMark.getExamId());
        if (Objects.equals(exam.getStatus(), "COMPLETED")) {
            throw new IllegalStateException("You cannot change mark for a completed exam.");
        }
        if (Objects.equals(exam.getStatus(), "PUBLISHED")) {
            throw new IllegalStateException("Exam must be in SCORED state to update scores");
        }
        if (updatedMark.isFinalized()) {
            throw new IllegalStateException("Cannot change finalized mark");
        }
        updatedMark.setScore(request.getScore());
        updatedMark.setScoredAt(LocalDateTime.now());
        updatedMark.setExaminerId(request.getExaminerId());
        markRepository.save(updatedMark);
        return MarkMapper.toResponse(updatedMark);
    }

    @KafkaListener(
            topics = "exam-complete-events",
            groupId = "mark-service-group"
    )
    public List<MarkResponse> finalizeMarkByExamId(String message) {
        System.out.println("Nhận complete exam event: " + message);
        List<Mark> markList = markRepository.findMarkByExamId(message);
        LocalDateTime now = LocalDateTime.now();
        for (Mark mark : markList) {
            if (!mark.isFinalized()) {
                mark.setFinalized(true);
                mark.setScoredAt(now);
            }
        }
        List<Mark> updatedMarks = markRepository.saveAll(markList);
        return updatedMarks.stream()
                .map(MarkMapper::toResponse)
                .collect(Collectors.toList());
    }

    @KafkaListener(
            topics = "exam-unregistration-events",
            groupId = "mark-service-group"
    )
    private void deleteKafkaMark(String message) {
        System.out.println("Nhận unregistration event: " + message);

        String[] parts = message.split(":");
        if (parts.length != 2) {
            System.err.println("Message không hợp lệ: " + message);
            return;
        }

        String candidateId = parts[0];
        String examId = parts[1];

        Mark mark= markRepository.findMarkByCandidateIdAndExamId(candidateId, examId)
                .orElseThrow(()-> new ResourceNotFoundException("Mark not found"));

        markRepository.delete(mark);
        System.out.println("Đã hủy đăng ký thành công");
    }

    @Override
    public boolean checkAllFinalizedMarkByExamId(String examId){
        ExamResponse exam = examServiceClient.getExamById(examId);
        return markRepository.existsByExamIdAndFinalizedFalse(examId);
    }

    @Override
    public void deleteMark(String id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Mark not found"));
        markRepository.delete(mark);
    }
}
