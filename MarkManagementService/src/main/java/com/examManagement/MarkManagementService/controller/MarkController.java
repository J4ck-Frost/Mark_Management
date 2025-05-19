package com.examManagement.MarkManagementService.controller;

import com.examManagement.MarkManagementService.dto.MarkRegisterRequest;
import com.examManagement.MarkManagementService.dto.MarkRequest;
import com.examManagement.MarkManagementService.dto.MarkResponse;
import com.examManagement.MarkManagementService.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marks")
@RequiredArgsConstructor
public class MarkController {
    private final MarkService markService;

    // Record a new exam score (prevents duplicates)
    @PostMapping
    public ResponseEntity<MarkResponse> createMark(@RequestBody @Valid MarkRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(markService.registerMark(request.getCandidateId(), request.getExamId()));
    }

    @GetMapping
    public ResponseEntity<List<MarkResponse>> getAllMarks(){
        return ResponseEntity.ok(markService.findAllMark());
    }

    // Get a mark by ID
    @GetMapping("/{id}")
    public ResponseEntity<MarkResponse> getMarkById(@PathVariable String id) {
        return ResponseEntity.ok(markService.findMarkById(id));
    }

    // Get all marks for an exam
    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<MarkResponse>> getMarksByExamId(@PathVariable String examId) {
        return ResponseEntity.ok(markService.findMarkByExamId(examId));
    }

    // Get all marks assigned by an examiner
    @GetMapping("/examiner/{examinerId}")
    public ResponseEntity<List<MarkResponse>> getMarksByExaminerId(@PathVariable String examinerId) {
        return ResponseEntity.ok(markService.findMarkByExaminerId(examinerId));
    }

    // Get all marks for a candidate
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<MarkResponse>> getMarksByCandidateId(@PathVariable String candidateId) {
        return ResponseEntity.ok(markService.findMarkByCandidateId(candidateId));
    }

    // Get a specific mark by candidate and exam
    @GetMapping("/candidate/{candidateId}/exam/{examId}")
    public ResponseEntity<MarkResponse> getMarkByCandidateAndExam(
            @PathVariable String candidateId,
            @PathVariable String examId
    ) {
        return ResponseEntity.ok(markService.findMarkByCandidateIdAndExamId(candidateId, examId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkResponse> updateMark(
            @PathVariable String id,
            @RequestBody @Valid MarkRequest request
    ) {
        return ResponseEntity.ok(markService.updateMark(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMark(@PathVariable String id) {
        markService.deleteMark(id);
        return ResponseEntity.noContent().build();
    }
}
