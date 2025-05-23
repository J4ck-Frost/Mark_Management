package com.examManagement.ExamAdministrationService.validator;

import com.examManagement.ExamAdministrationService.entity.Exam;
import com.examManagement.ExamAdministrationService.entity.ExamStatus;
import com.examManagement.ExamAdministrationService.exception.ResourceNotFoundException;
import com.examManagement.ExamAdministrationService.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExamValidator {
    private final ExamRepository examRepository;

    public void validateExamStatusForPublish(Exam exam) {
        if (exam.getStatus() != ExamStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT exams can be published");
        }
    }

    public void validateExamStatusForScoring(Exam exam) {
        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new IllegalStateException("Only PUBLISHED exams can be scored");
        }
    }

    public void validateExamStatusForCompletion(Exam exam) {
        if (exam.getStatus() != ExamStatus.SCORED) {
            throw new IllegalStateException("Only SCORED exams can be completed");
        }
    }

    public void validateExamStatusForCancellation(Exam exam) {
        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new IllegalStateException("Only PUBLISHED exams can be canceled");
        }
    }

    public void validateExamsForBatchPublish(List<Exam> exams, List<String> examIds) {
        if (exams.size() != examIds.size()) {
            List<String> foundIds = exams.stream().map(Exam::getId).toList();
            List<String> missingIds = examIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("Missing exams: " + missingIds);
        }

        exams.forEach(exam -> {
            if (exam.getStatus() != ExamStatus.DRAFT) {
                throw new IllegalStateException(
                        "Exam " + exam.getId() + " is not in DRAFT state"
                );
            }
        });
    }
}
