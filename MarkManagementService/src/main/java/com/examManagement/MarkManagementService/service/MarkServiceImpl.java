package com.examManagement.MarkManagementService.service;

import com.examManagement.MarkManagementService.client.ExamServiceClient;
import com.examManagement.MarkManagementService.dto.ExamResponse;
import com.examManagement.MarkManagementService.dto.MarkRequest;
import com.examManagement.MarkManagementService.dto.MarkResponse;
import com.examManagement.MarkManagementService.entity.Mark;
import com.examManagement.MarkManagementService.exception.ResourceNotFoundException;
import com.examManagement.MarkManagementService.mapper.MarkMapper;
import com.examManagement.MarkManagementService.repository.MarkRepository;
import examManagement.common.dto.CandidateEvent;
import examManagement.common.dto.ExamEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MarkServiceImpl implements MarkService{
    private final MarkRepository markRepository;
    private final ExamServiceClient examServiceClient;
    private final MarkMapper markMapper;

    @KafkaListener(
            topics = "exam-registration-events",
            groupId = "mark-service-group"
    )
    public void registerMark(CandidateEvent event) {
        log.info("Nhận event: {}", event); // Log để debug

        String candidateId = event.candidateId();
        String examId = event.examId();
        Mark mark= new Mark();
        mark.setCandidateId(candidateId);
        mark.setExamId(examId);
        mark.setRegisteredAt(LocalDateTime.now());
        markRepository.save(mark);
        log.info("Register for this exam successfully");
    }

    @Override
    public List<MarkResponse> findAllMark() {
        return markRepository.findAll().stream()
                .map(markMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MarkResponse findMarkById(String id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Mark not found"));
        return markMapper.toResponse(mark);
    }

    @Override
    public List<MarkResponse> findMarkByExamId(String examId) {
        return markRepository.findMarkByExamId(examId).stream()
                .map(markMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkResponse> findMarkByExaminerId(String examinerId) {
        return markRepository.findMarkByExaminerId(examinerId).stream()
                .map(markMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarkResponse> findMarkByCandidateId(String candidateId) {
        return markRepository.findMarkByCandidateId(candidateId).stream()
                .map(markMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MarkResponse findMarkByCandidateIdAndExamId(String candidateId, String examId) {
        Mark mark = markRepository.findMarkByCandidateIdAndExamId(candidateId, examId)
                .orElseThrow(()-> new ResourceNotFoundException("Mark not found"));
        return markMapper.toResponse(mark);
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
        return markMapper.toResponse(updatedMark);
    }

    @KafkaListener(
            topics = "exam-completion-events",
            groupId = "mark-service-group"
    )
    public List<MarkResponse> finalizeMarkByExamId(ExamEvent event) {
        log.info("Nhận complete exam event: {}" , event);
        String examId = event.examId();
        List<Mark> marks = markRepository.findMarkByExamIdAndFinalizedFalse(examId);
        if (marks.isEmpty()) {
            return Collections.emptyList();
        }
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        for (Mark mark : marks) {
                mark.setFinalized(true);
                mark.setScoredAt(now);
        }
        List<Mark> updatedMarks = markRepository.saveAll(marks);
        return updatedMarks.stream()
                .map(markMapper::toResponse)
                .collect(Collectors.toList());
    }

    @KafkaListener(
            topics = "exam-unregistration-events",
            groupId = "mark-service-group"
    )
    public void deleteKafkaMark(CandidateEvent event) {
        log.info("Nhận unregistration event: {}",  event);


        String candidateId = event.candidateId();
        String examId = event.examId();

        Mark mark= markRepository.findMarkByCandidateIdAndExamId(candidateId, examId)
                .orElseThrow(()-> new ResourceNotFoundException("You haven't registered for this exam"));

        markRepository.delete(mark);
        log.info("Unregister this event successfully");
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
