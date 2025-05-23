package com.examManagement.ExamAdministrationService.Mapper;

import com.examManagement.ExamAdministrationService.dto.ExamRequest;
import com.examManagement.ExamAdministrationService.dto.ExamResponse;
import com.examManagement.ExamAdministrationService.entity.Exam;


public class ExamMapper {
    public static Exam toEntity(ExamRequest request) {
        Exam exam = new Exam();
        exam.setName(request.getName());
        exam.setScheduledTime(request.getScheduledTime());
        exam.setDuration(request.getDuration());
        exam.setDescription(request.getDescription());
        exam.setAssignedExaminerId(request.getAssignedExaminerId());
        exam.setLocation(request.getLocation());
        return exam;
    }

    public static ExamResponse toResponse(Exam exam) {
        ExamResponse response = new ExamResponse();
        response.setId(exam.getId());
        response.setName(exam.getName());
        response.setScheduledTime(exam.getScheduledTime());
        response.setDuration(exam.getDuration());
        response.setDescription(exam.getDescription());
        response.setAssignedExaminerId(exam.getAssignedExaminerId());
        response.setStatus(String.valueOf(exam.getStatus()));
        response.setLocation(exam.getLocation());
        return response;
    }
}
