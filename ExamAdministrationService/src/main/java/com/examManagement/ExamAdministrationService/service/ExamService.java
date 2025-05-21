package com.examManagement.ExamAdministrationService.service;

import com.examManagement.ExamAdministrationService.dto.ExamRequest;
import com.examManagement.ExamAdministrationService.dto.ExamResponse;

import java.util.List;

public interface ExamService {
    ExamResponse createExam(ExamRequest request);
    List<ExamResponse> getAllExams();
    ExamResponse getExamById(String id);
    ExamResponse updateExam(String id, ExamRequest request);
    void deleteExam(String id);
    List<ExamResponse> getExamsByExaminerId(String examinerId);
    ExamResponse publishExam(String examId);
    ExamResponse scoreExam(String examId);
    ExamResponse completeExam(String examId);
    ExamResponse cancelExam(String examId);
    ExamResponse revertToDraft(String examId);
    List<ExamResponse> batchPublishExams(List<String> examIds);
}
