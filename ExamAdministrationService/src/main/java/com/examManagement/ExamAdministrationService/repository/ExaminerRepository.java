package com.examManagement.ExamAdministrationService.repository;

import com.examManagement.ExamAdministrationService.entity.Examiner;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExaminerRepository extends MongoRepository<Examiner, String> {
    List<Examiner> findExaminerByName(String name);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsById(String id);
    boolean existsByIdAndActiveTrue(String id);
}
