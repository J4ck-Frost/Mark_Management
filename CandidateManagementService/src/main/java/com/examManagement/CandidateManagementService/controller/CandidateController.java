package com.examManagement.CandidateManagementService.controller;

import com.examManagement.CandidateManagementService.dto.CandidateRequest;
import com.examManagement.CandidateManagementService.dto.CandidateResponse;
import com.examManagement.CandidateManagementService.dto.CandidateUpdateInfoRequest;
import com.examManagement.CandidateManagementService.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/candidates")
@RestController
@RequiredArgsConstructor
public class CandidateController {

        private final CandidateService candidateService;


        @GetMapping
        public ResponseEntity<List<CandidateResponse>> getAllCandidates() {
            return ResponseEntity.ok(candidateService.getAllCandidates());
        }

        @PostMapping("/register")
        public ResponseEntity<CandidateResponse> registerCandidate(@Valid @RequestBody CandidateRequest request) {
            CandidateResponse candidate = candidateService.registerCandidate(request);
            return ResponseEntity.ok(candidate);
        }

        @GetMapping("/{id}")
        public ResponseEntity<CandidateResponse> getCandidateById(@Valid @PathVariable String id) {
            return ResponseEntity.ok(candidateService.getCandidateById(id));
        }

        @PutMapping("/{id}")
        public ResponseEntity<CandidateResponse> updateCandidate(@PathVariable String id, @Valid @RequestBody CandidateUpdateInfoRequest request) {
            return ResponseEntity.ok(candidateService.updateCandidateInfo(id, request));
        }

        @PutMapping("/{examId}/{candidateId}/unregister")
        public ResponseEntity<CandidateResponse> unregisterCandidate(@PathVariable String examId, @PathVariable String candidateId) {
            return ResponseEntity.ok(candidateService.unregisterCandidate(examId, candidateId));
        }
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteCandidate(@PathVariable String id) {
            candidateService.deleteCandidate(id);
            return ResponseEntity.noContent().build();
        }
}
