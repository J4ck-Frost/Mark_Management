package com.examManagement.CandidateManagementService.controller;

import com.examManagement.CandidateManagementService.dto.CandidateRequest;
import com.examManagement.CandidateManagementService.dto.CandidateResponse;
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

        @PostMapping
        public ResponseEntity<CandidateResponse> registerCandidate(@Valid @RequestBody CandidateRequest request) {
            return ResponseEntity.ok(candidateService.registerCandidate(request));
        }

        @GetMapping("/{id}")
        public ResponseEntity<CandidateResponse> getCandidateById(@Valid @PathVariable String id) {
            return ResponseEntity.ok(candidateService.getCandidateById(id));
        }

        @PutMapping("/{id}")
        public ResponseEntity<CandidateResponse> updateCandidate(@PathVariable String id, @Valid @RequestBody CandidateRequest request) {
            return ResponseEntity.ok(candidateService.updateCandidate(id, request));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteCandidate(@PathVariable String id) {
            candidateService.deleteCandidate(id);
            return ResponseEntity.noContent().build();
        }
}
