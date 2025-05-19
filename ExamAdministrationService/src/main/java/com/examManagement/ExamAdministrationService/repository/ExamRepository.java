package com.examManagement.ExamAdministrationService.repository;

import com.examManagement.ExamAdministrationService.entity.Exam;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExamRepository extends MongoRepository<Exam, String> {
    List<Exam> findByAssignedExaminerId(String id);
}
