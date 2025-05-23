package com.examManagement.MarkManagementService.repository;

import com.examManagement.MarkManagementService.entity.Mark;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MarkRepository extends MongoRepository<Mark, String> {
    List<Mark> findMarkByExamId(String examId);
    List<Mark> findMarkByCandidateId(String candidateId);
    List<Mark> findMarkByExaminerId(String examinerId);
    List<Mark> findMarkByExamIdAndFinalizedFalse(String examId);
    Optional<Mark> findMarkByCandidateIdAndExamId(String candidateId, String examId);
    boolean existsByCandidateIdAndExamId(String candidateId, String examId);
    boolean existsByExamIdAndFinalizedFalse(String examId);


}
