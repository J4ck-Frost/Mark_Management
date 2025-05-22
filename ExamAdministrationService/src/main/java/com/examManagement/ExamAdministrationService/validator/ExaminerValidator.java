package com.examManagement.ExamAdministrationService.validator;

import com.examManagement.ExamAdministrationService.entity.Exam;
import com.examManagement.ExamAdministrationService.repository.ExamRepository;
import com.examManagement.ExamAdministrationService.repository.ExaminerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExaminerValidator {
    private final ExaminerRepository examinerRepository;
    private final ExamRepository examRepository;

    public void validateExaminerEmail(String email) {
        if (examinerRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already exists: " + email);
        }
    }

    public void validateExaminerPhoneNumber(String phoneNumber) {
        if (examinerRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalStateException("Phone number already exists: " + phoneNumber);
        }
    }

    public void validateExaminerNotAssignedToExams(String examinerId) {
        List<Exam> assignedExams = examRepository.findByAssignedExaminerId(examinerId);
        if (!assignedExams.isEmpty()) {
            String assignedExamIds = assignedExams.stream()
                    .map(Exam::getId)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException(
                    "Cannot inactive examiner assigned to exam(s): " + assignedExamIds
            );
        }
    }
}
