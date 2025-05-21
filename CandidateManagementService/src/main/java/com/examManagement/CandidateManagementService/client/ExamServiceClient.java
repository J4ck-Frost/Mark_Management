package com.examManagement.CandidateManagementService.client;

import com.examManagement.CandidateManagementService.dto.ExamResponse;
import com.examManagement.CandidateManagementService.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class ExamServiceClient {

    private final WebClient webClient;

    public ExamServiceClient() {
        // Tạo WebClient với base URL
        this.webClient = WebClient.create("http://localhost:8081");
    }

    public ExamResponse getExamById(String id) {
        try {
            return webClient.get()
                    .uri("/api/exams/{id}", id)
                    .retrieve()
                    .bodyToMono(ExamResponse.class)
                    .block(); // chặn luồng để lấy dữ liệu đồng bộ
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResourceNotFoundException("Exam with ID " + id + " not found.");
        }
    }
}
