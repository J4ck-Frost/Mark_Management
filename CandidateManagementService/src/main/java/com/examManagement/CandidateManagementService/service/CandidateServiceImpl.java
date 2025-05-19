package com.examManagement.CandidateManagementService.service;

import com.examManagement.CandidateManagementService.dto.CandidateRequest;
import com.examManagement.CandidateManagementService.dto.CandidateResponse;
import com.examManagement.CandidateManagementService.entity.Candidate;
import com.examManagement.CandidateManagementService.exception.ResourceNotFoundException;
import com.examManagement.CandidateManagementService.mapper.CandidateMapper;
import com.examManagement.CandidateManagementService.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateServiceImpl implements CandidateService{
    private final CandidateRepository candidateRepository;
    @Override
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(CandidateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateResponse registerCandidate(CandidateRequest request) {
        if (candidateRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (candidateRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone Number already exists");
        }
        Candidate candidate = CandidateMapper.toEntity(request);
        candidateRepository.save(candidate);
        return CandidateMapper.toResponse(candidate);
    }

    @Override
    public CandidateResponse getCandidateById(String id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        return CandidateMapper.toResponse(candidate);
    }

    @Override
    public CandidateResponse updateCandidate(String id, CandidateRequest request) {
        Candidate updatedCandidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        if (!updatedCandidate.getEmail().equals(request.getEmail()) &&
                candidateRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (!updatedCandidate.getPhoneNumber().equals(request.getPhoneNumber()) &&
                candidateRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone Number already exists");
        }
        updatedCandidate.setFullName(request.getFullName());
        updatedCandidate.setPhoneNumber(request.getPhoneNumber());
        updatedCandidate.setEmail(request.getEmail());
        updatedCandidate.setGender(request.isGender());
        updatedCandidate.setExamIds(request.getExamIds());
        candidateRepository.save(updatedCandidate);
        return CandidateMapper.toResponse(updatedCandidate);
    }

    @Override
    public void deleteCandidate(String id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        candidateRepository.delete(candidate);
    }

    @Override
    public void validateCandidateById(List<String> candidateIds) {
        List<String> missingIds = candidateIds.stream()
                .filter(id -> !candidateRepository.existsById(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Examiners not found with IDs: " + missingIds
            );
        }
    }
}
