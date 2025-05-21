package com.examManagement.MarkManagementService.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkRequest {
    @NotBlank
    private String examinerId;
    @Max(value = 10, message = "Score must be <=10")
    @Min(value = 0, message = "Score must be >=0")
    private double score;
    private boolean finalized;
}
