package com.examManagement.MarkManagementService.client;

import com.examManagement.MarkManagementService.dto.ExamResponse;
import com.examManagement.MarkManagementService.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.Cacheable;
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
