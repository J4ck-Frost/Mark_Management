package com.examManagement.MarkManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponse implements Serializable {
    private String id;
    private String name;
    private String description;
    private LocalDateTime scheduledTime;
    private int duration;
    private String status;
    private List<String> assignedExaminerId;
    private String location;
}
