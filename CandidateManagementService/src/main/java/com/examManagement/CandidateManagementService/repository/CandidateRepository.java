package com.examManagement.CandidateManagementService.repository;

import com.examManagement.CandidateManagementService.entity.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CandidateRepository extends MongoRepository<Candidate, String> {
    List<Candidate> findByFullName(String fullName);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsById(String id );
}
