package com.examManagement.ExamAdministrationService.controller;

import com.examManagement.ExamAdministrationService.dto.ExamRequest;
import com.examManagement.ExamAdministrationService.dto.ExamResponse;
import com.examManagement.ExamAdministrationService.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/exams")
@RestController
public class ExamController {
    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping
    public ResponseEntity<ExamResponse> createExam(@RequestBody @Valid ExamRequest request) {
        ExamResponse created = examService.createExam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ExamResponse>> getAllExams() {
        return ResponseEntity.ok(examService.getAllExams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable String id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamResponse> updateExam(
            @PathVariable String id,
            @RequestBody @Valid ExamRequest request) {
        return ResponseEntity.ok(examService.updateExam(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable String id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/examiner/{examinerId}")
    public ResponseEntity<List<ExamResponse>> getExamsByExaminerId(@PathVariable String examinerId) {
        return ResponseEntity.ok(examService.getExamsByExaminerId(examinerId));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ExamResponse> publishExam(@PathVariable String id) {
        return ResponseEntity.ok(examService.publishExam(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ExamResponse> completeExam(@PathVariable String id) {
        return ResponseEntity.ok(examService.completeExam(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ExamResponse> cancelExam(@PathVariable String id) {
        return ResponseEntity.ok(examService.cancelExam(id));
    }

    @PostMapping("/{id}/revert-to-draft")
    public ResponseEntity<ExamResponse> revertToDraft(@PathVariable String id) {
        return ResponseEntity.ok(examService.revertToDraft(id));
    }

    @PostMapping("/batch-publish")
    public ResponseEntity<List<ExamResponse>> publishExams(@RequestBody List<String> examIds) {
        return ResponseEntity.ok(examService.batchPublishExams(examIds));
    }
}

