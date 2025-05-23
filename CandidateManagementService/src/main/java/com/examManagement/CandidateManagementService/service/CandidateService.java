package com.examManagement.CandidateManagementService.service;

import com.examManagement.CandidateManagementService.dto.CandidateRequest;
import com.examManagement.CandidateManagementService.dto.CandidateResponse;


import java.util.List;

public interface CandidateService {
    List<CandidateResponse> getAllCandidates();
    CandidateResponse registerCandidate(String examId, CandidateRequest request);
    CandidateResponse unregisterCandidate(String examId, String candidateId);
    CandidateResponse getCandidateById(String id);
    CandidateResponse updateCandidateInfo(String id, CandidateRequest request);
    void deleteCandidate(String id);
}

