package com.examManagement.ExamAdministrationService.Mapper;

import com.examManagement.ExamAdministrationService.dto.ExaminerRequest;
import com.examManagement.ExamAdministrationService.dto.ExaminerResponse;
import com.examManagement.ExamAdministrationService.entity.Examiner;

public class ExaminerMapper {
    public static Examiner toEntity(ExaminerRequest request) {
        Examiner examiner = new Examiner();
        examiner.setName(request.getName());
        examiner.setEmail(request.getEmail());
        examiner.setPhoneNumber(request.getPhoneNumber());
        return examiner;
    }

    public static ExaminerResponse toResponse(Examiner examiner) {
        ExaminerResponse response = new ExaminerResponse();
        response.setId(examiner.getId());
        response.setName(examiner.getName());
        response.setEmail(examiner.getEmail());
        response.setPhoneNumber(examiner.getPhoneNumber());
        response.setActive(examiner.isActive());
        return response;
    }
}
