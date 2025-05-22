package com.examManagement.CandidateManagementService.service;

import com.examManagement.CandidateManagementService.client.ExamServiceClient;
import com.examManagement.CandidateManagementService.dto.CandidateRequest;
import com.examManagement.CandidateManagementService.dto.CandidateResponse;
import com.examManagement.CandidateManagementService.dto.CandidateUpdateInfoRequest;
import com.examManagement.CandidateManagementService.dto.ExamResponse;
import com.examManagement.CandidateManagementService.entity.Candidate;
import com.examManagement.CandidateManagementService.exception.ResourceNotFoundException;
import com.examManagement.CandidateManagementService.mapper.CandidateMapper;
import com.examManagement.CandidateManagementService.repository.CandidateRepository;
import com.examManagement.CandidateManagementService.validator.CandidateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateServiceImpl implements CandidateService{
    private final KafkaTemplate<String, String > kafkaTemplate;
    private final CandidateRepository candidateRepository;
    private final ExamServiceClient examServiceClient;
    private final CandidateValidator candidateValidator;

    @Override
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(CandidateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateResponse registerCandidate(CandidateRequest request) {
        ExamResponse exam = examServiceClient.getExamById(request.getExamId());
        if (!Objects.equals(exam.getStatus(), "PUBLISHED")) {
            throw new IllegalStateException("You can only register for a published exam.");
        }
        if (candidateRepository.existsByIdCard(request.getIdCard())) {
            Candidate candidate = candidateRepository.findByIdCard(request.getIdCard());
            if (isCandidateInfoChanged(candidate, request)) {
                CandidateMapper.updateFromRequest(candidate, CandidateMapper.toUpdateRequest(request));
            }
            if (!candidate.getExamIds().add(request.getExamId())) {
                throw new IllegalStateException("Candidate is already registered for this exam");
            }
            candidateRepository.save(candidate);
            sendKafkaEvent(candidate.getId(), request.getExamId(), "exam-registration-events");
            return CandidateMapper.toResponse(candidate);
        }
        // Nếu ứng viên chưa tồn tại
        candidateValidator.validateCandidatePhoneNumber(request.getPhoneNumber());
        candidateValidator.validateCandidateEmail(request.getEmail());
        Candidate candidate = CandidateMapper.toEntity(request);
        candidateRepository.save(candidate);
        sendKafkaEvent(candidate.getId(), request.getExamId(), "exam-registration-events");
        return CandidateMapper.toResponse(candidate);
    }

    @Override
    public CandidateResponse unregisterCandidate(String examId, String candidateId){
        ExamResponse exam = examServiceClient.getExamById(examId);
        if (Objects.equals(exam.getStatus(), "COMPLETED")) {
            throw new IllegalStateException("You cannot unregister for a completed exam.");
        }
        else if (Objects.equals(exam.getStatus(), "SCORED")) {
            throw new IllegalStateException("Exam is being scored, cannot unregister");
        }
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        if (!candidate.getExamIds().remove(examId)) {
            throw new IllegalArgumentException("Candidate is not registered for this exam");
        }
        candidateRepository.save(candidate);
        sendKafkaEvent(candidateId, examId, "exam-unregistration-events");
        return CandidateMapper.toResponse(candidate);
    }

    @Override
    public CandidateResponse getCandidateById(String id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        return CandidateMapper.toResponse(candidate);
    }

    @Override
    public CandidateResponse updateCandidateInfo(String id, CandidateUpdateInfoRequest request) {
        Candidate updatedCandidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        if (!updatedCandidate.getIdCard().equals(request.getIdCard()) &&
                candidateRepository.existsByIdCard(request.getIdCard())) {
            throw new IllegalArgumentException("Id card already exists");
        }
        if (!updatedCandidate.getEmail().equals(request.getEmail())) {
            candidateValidator.validateCandidateEmail(request.getEmail());
        }
        if (!updatedCandidate.getPhoneNumber().equals(request.getPhoneNumber())){
            candidateValidator.validateCandidatePhoneNumber(request.getPhoneNumber());
        }
        CandidateMapper.updateFromRequest(updatedCandidate, request);
        candidateRepository.save(updatedCandidate);
        return CandidateMapper.toResponse(updatedCandidate);
    }

    @Override
    public void deleteCandidate(String id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        candidateRepository.delete(candidate);
    }

    private boolean isCandidateInfoChanged(Candidate candidate, CandidateRequest request) {
        return !candidate.getFullName().equals(request.getFullName()) ||
                !candidate.getEmail().equals(request.getEmail()) ||
                !candidate.getPhoneNumber().equals(request.getPhoneNumber()) ||
                candidate.isGender() != request.isGender();
    }

    private void sendKafkaEvent(String candidateId, String examId, String topic) {
        try {
            kafkaTemplate.send(topic, candidateId + ":" + examId);
            System.out.println("✅ Đã gửi message vào Kafka topic.");
        } catch (Exception ex) {
            System.err.println("❌ Lỗi khi gửi Kafka: " + ex.getMessage());
        }
    }
}
