package com.examManagement.ExamAdministrationService.validation;

import com.examManagement.ExamAdministrationService.repository.ExaminerRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UniquePhoneValidator implements ConstraintValidator<UniquePhone, String> {
    private final ExaminerRepository examinerRepository;

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        return phoneNumber != null && !examinerRepository.existsByPhoneNumber(phoneNumber);
    }
}
