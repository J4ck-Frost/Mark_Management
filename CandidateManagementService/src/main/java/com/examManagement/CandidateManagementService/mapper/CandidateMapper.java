package com.examManagement.CandidateManagementService.mapper;

import com.examManagement.CandidateManagementService.dto.CandidateRequest;
import com.examManagement.CandidateManagementService.dto.CandidateResponse;
import com.examManagement.CandidateManagementService.entity.Candidate;

public class CandidateMapper {
    public static Candidate toEntity(CandidateRequest request) {
        Candidate candidate = new Candidate();
        candidate.setFullName(request.getFullName());
        candidate.setEmail(request.getEmail());
        candidate.setPhoneNumber(request.getPhoneNumber());
        candidate.setGender(request.isGender());
        candidate.setExamIds(request.getExamIds());
        return candidate;
    }

    public static CandidateResponse toResponse(Candidate candidate) {
        CandidateResponse response = new CandidateResponse();
        response.setId(candidate.getId());
        response.setFullName(candidate.getFullName());
        response.setEmail(candidate.getEmail());
        response.setPhoneNumber(candidate.getPhoneNumber());
        response.setGender(candidate.isGender());
        response.setExamIds(candidate.getExamIds());
        return response;
    }
}
