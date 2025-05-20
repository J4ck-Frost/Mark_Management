package com.examManagement.CandidateManagementService.service;

import com.examManagement.CandidateManagementService.dto.CandidateRequest;
import com.examManagement.CandidateManagementService.dto.CandidateResponse;
import com.examManagement.CandidateManagementService.dto.CandidateUpdateInfoRequest;
import com.examManagement.CandidateManagementService.entity.Candidate;
import com.examManagement.CandidateManagementService.exception.ResourceNotFoundException;
import com.examManagement.CandidateManagementService.mapper.CandidateMapper;
import com.examManagement.CandidateManagementService.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateServiceImpl implements CandidateService{
    private final KafkaTemplate<String, String > kafkaTemplate;
    private final CandidateRepository candidateRepository;

    @Override
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(CandidateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateResponse registerCandidate(CandidateRequest request) {
        if (candidateRepository.existsByIdCard(request.getIdCard())) {
            Candidate candidate = candidateRepository.findByIdCard(request.getIdCard());
            if (isCandidateInfoChanged(candidate, request)) {
                CandidateResponse candidateResponse = updateCandidateInfo(candidate.getId(), CandidateMapper.toUpdateRequest(request));
                sendKafkaEvent(candidate.getId(), request.getExamId(), "exam-registration-events");
                return candidateResponse;
            } else {
                List<String> listExamIds = candidate.getExamIds();
                listExamIds.add(request.getExamId());
                candidate.setExamIds(listExamIds);
                candidateRepository.save(candidate);
                sendKafkaEvent(candidate.getId(), request.getExamId(), "exam-registration-events");
                return CandidateMapper.toResponse(candidate);
            }
        }
        // Nếu ứng viên chưa tồn tại
        Candidate candidate = CandidateMapper.toEntity(request);
        candidateRepository.save(candidate);
        sendKafkaEvent(candidate.getId(), request.getExamId(), "exam-registration-events");
        return CandidateMapper.toResponse(candidate);
    }



    @Override
    public CandidateResponse unregisterCandidate(String examId, String candidateId){
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        List<String> listExamIds = candidate.getExamIds();
        if (!listExamIds.contains(examId)) {
            throw new IllegalArgumentException("Candidate is not registered for this exam");
        }
        listExamIds.remove(examId);
        candidate.setExamIds(listExamIds);
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
        if (!updatedCandidate.getEmail().equals(request.getEmail()) &&
                candidateRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (!updatedCandidate.getPhoneNumber().equals(request.getPhoneNumber()) &&
                candidateRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone Number already exists");
        }

        updatedCandidate.setIdCard(request.getIdCard());
        updatedCandidate.setFullName(request.getFullName());
        updatedCandidate.setPhoneNumber(request.getPhoneNumber());
        updatedCandidate.setEmail(request.getEmail());
        updatedCandidate.setGender(request.isGender());
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
