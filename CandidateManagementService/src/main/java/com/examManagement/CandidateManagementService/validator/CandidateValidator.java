package com.examManagement.CandidateManagementService.validator;

import com.examManagement.CandidateManagementService.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CandidateValidator {
    private final CandidateRepository candidateRepository;
    public void validateCandidateEmail(String email) {
        if (candidateRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already exists: " + email);
        }
    }

    public void validateCandidatePhoneNumber(String phoneNumber) {
        if (candidateRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalStateException("Phone number already exists: " + phoneNumber);
        }
    }
}
