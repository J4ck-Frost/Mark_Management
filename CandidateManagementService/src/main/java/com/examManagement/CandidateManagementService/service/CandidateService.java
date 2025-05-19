package com.examManagement.CandidateManagementService.service;

import com.examManagement.CandidateManagementService.dto.CandidateRequest;
import com.examManagement.CandidateManagementService.dto.CandidateResponse;

import java.util.List;

public interface CandidateService {
    List<CandidateResponse> getAllCandidates();
    CandidateResponse registerCandidate(CandidateRequest request);
    CandidateResponse getCandidateById(String id);
    CandidateResponse updateCandidate(String id, CandidateRequest request);
    void deleteCandidate(String id);
    void validateCandidateById(List<String> id);
}

