package com.examManagement.MarkManagementService.service;

import com.examManagement.MarkManagementService.dto.MarkRequest;
import com.examManagement.MarkManagementService.dto.MarkResponse;

import java.util.List;

public interface MarkService {
    void registerMark(String message);
    List<MarkResponse> findAllMark();
    MarkResponse findMarkById (String id);
    List<MarkResponse> findMarkByExamId (String examId);
    List<MarkResponse> findMarkByExaminerId (String examinerId);
    List<MarkResponse> findMarkByCandidateId (String candidateId);
    MarkResponse findMarkByCandidateIdAndExamId (String candidateId, String examId);
    MarkResponse updateMark(String id, MarkRequest request);
    void deleteMark(String id);

}
