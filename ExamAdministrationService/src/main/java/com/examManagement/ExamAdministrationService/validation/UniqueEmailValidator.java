package com.examManagement.ExamAdministrationService.validation;

import com.examManagement.ExamAdministrationService.repository.ExaminerRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final ExaminerRepository examinerRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && !examinerRepository.existsByEmail(email);
    }
}
