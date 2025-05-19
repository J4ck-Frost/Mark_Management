package com.examManagement.MarkManagementService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document (collection = "marks")
public class Mark {
    @Id
    private String id;

    private String examId;
    private String candidateId;
    private LocalDateTime registeredAt;
    private double score;
    private boolean finalized;
    private LocalDateTime scoredAt;
    private String examinerId;
}
