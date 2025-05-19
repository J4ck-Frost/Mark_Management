package com.examManagement.MarkManagementService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkRegisterRequest {
    @NotBlank
    private String examId;
    @NotBlank
    private String candidateId;
}
