package com.examManagement.CandidateManagementService.service;

import com.examManagement.CandidateManagementService.client.ExamServiceClient;
import com.examManagement.CandidateManagementService.dto.CandidateRequest;
import com.examManagement.CandidateManagementService.dto.CandidateResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateServiceImpl implements CandidateService{
    private final KafkaTemplate<String, String > kafkaTemplate;
    private final CandidateRepository candidateRepository;
    private final ExamServiceClient examServiceClient;
    private final CandidateValidator candidateValidator;
    private final CandidateMapper candidateMapper;

    @Override
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(candidateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateResponse registerCandidate(String examId, CandidateRequest request) {
        ExamResponse exam = examServiceClient.getExamById(examId);
        if (!Objects.equals(exam.getStatus(), "PUBLISHED")) {
            throw new IllegalStateException("You can only register for a published exam.");
        }

        Optional<Candidate> existingCandidateOpt = candidateRepository.findByIdCard(request.getIdCard());

        Candidate candidate;

        if (existingCandidateOpt.isPresent()) {
            candidate = existingCandidateOpt.get();
            candidateMapper.updateFromRequest(candidate, request);
            List<String> examIds = candidate.getExamIds();
            if (examIds != null && examIds.contains(examId)) {
                throw new IllegalStateException("Candidate is already registered for this exam.");
            }

            if (examIds == null) {
                examIds = new ArrayList<>();
            }
            examIds.add(examId);
            candidate.setExamIds(examIds);
        } else {
            // Ứng viên chưa tồn tại, validate trước khi tạo mới
            candidateValidator.validateCandidatePhoneNumber(request.getPhoneNumber());
            candidateValidator.validateCandidateEmail(request.getEmail());

            candidate = candidateMapper.toEntity(request);
            candidate.setExamIds(new ArrayList<>(List.of(examId)));
        }

        candidateRepository.save(candidate);
        sendKafkaEvent(candidate.getId(), examId, "exam-registration-events");
        return candidateMapper.toResponse(candidate);
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
        return candidateMapper.toResponse(candidate);
    }

    @Override
    public CandidateResponse getCandidateById(String id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        return candidateMapper.toResponse(candidate);
    }

    @Override
    public CandidateResponse updateCandidateInfo(String id, CandidateRequest request) {
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
        candidateMapper.updateFromRequest(updatedCandidate, request);
        candidateRepository.save(updatedCandidate);
        return candidateMapper.toResponse(updatedCandidate);
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
