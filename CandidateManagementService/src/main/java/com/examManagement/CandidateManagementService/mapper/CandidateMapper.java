package com.examManagement.CandidateManagementService.mapper;

import com.examManagement.CandidateManagementService.dto.CandidateRequest;
import com.examManagement.CandidateManagementService.dto.CandidateResponse;
import com.examManagement.CandidateManagementService.dto.CandidateUpdateInfoRequest;
import com.examManagement.CandidateManagementService.entity.Candidate;

import java.util.List;

public class CandidateMapper {
    public static Candidate toEntity(CandidateRequest request) {
        Candidate candidate = new Candidate();
        candidate.setIdCard(request.getIdCard());
        candidate.setFullName(request.getFullName());
        candidate.setEmail(request.getEmail());
        candidate.setPhoneNumber(request.getPhoneNumber());
        candidate.setGender(request.isGender());
        List<String> listExamIds = candidate.getExamIds();
        listExamIds.add(request.getExamId());
        candidate.setExamIds(listExamIds);
        return candidate;
    }

    public static CandidateResponse toResponse(Candidate candidate) {
        CandidateResponse response = new CandidateResponse();
        response.setId(candidate.getId());
        response.setIdCard(candidate.getIdCard());
        response.setFullName(candidate.getFullName());
        response.setEmail(candidate.getEmail());
        response.setPhoneNumber(candidate.getPhoneNumber());
        response.setGender(candidate.isGender());
        response.setExamIds(candidate.getExamIds());
        return response;
    }

    public static CandidateUpdateInfoRequest toUpdateRequest(CandidateRequest request){
        CandidateUpdateInfoRequest updateInfoRequest = new CandidateUpdateInfoRequest();
        updateInfoRequest.setEmail(request.getEmail());
        updateInfoRequest.setIdCard(request.getIdCard());
        updateInfoRequest.setPhoneNumber(request.getPhoneNumber());
        updateInfoRequest.setFullName(request.getFullName());
        updateInfoRequest.setGender(request.isGender());
        return  updateInfoRequest;
    }

    public static Candidate toUpdatedEntity(CandidateUpdateInfoRequest request) {
        Candidate candidate = new Candidate();
        candidate.setIdCard(request.getIdCard());
        candidate.setFullName(request.getFullName());
        candidate.setEmail(request.getEmail());
        candidate.setPhoneNumber(request.getPhoneNumber());
        candidate.setGender(request.isGender());
        return candidate;
    }


}
