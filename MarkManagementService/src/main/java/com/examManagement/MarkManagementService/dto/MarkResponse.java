package com.examManagement.MarkManagementService.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkResponse {
    private String id;
    private String examId;
    private String candidateId;
    private String examinerId;
    private double score;
    private boolean finalized;
    private LocalDateTime scoredAt;
    private LocalDateTime registeredAt;
}
