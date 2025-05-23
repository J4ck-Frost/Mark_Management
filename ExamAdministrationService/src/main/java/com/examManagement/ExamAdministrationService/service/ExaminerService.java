package com.examManagement.ExamAdministrationService.service;

import com.examManagement.ExamAdministrationService.dto.ExaminerRequest;
import com.examManagement.ExamAdministrationService.dto.ExaminerResponse;
import com.examManagement.ExamAdministrationService.dto.ExaminerUpdateRequest;

import java.util.List;

public interface ExaminerService {
    ExaminerResponse createExaminer(ExaminerRequest request);
    List<ExaminerResponse> getAllExaminers();
    ExaminerResponse getExaminerById(String id);
    ExaminerResponse updateExaminer(String id, ExaminerUpdateRequest request);
    ExaminerResponse inactiveExaminer(String id);
    void validateExaminerIds(List<String> examinerIds);
}
