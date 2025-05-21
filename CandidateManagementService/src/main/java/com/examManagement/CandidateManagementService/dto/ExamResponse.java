package com.examManagement.CandidateManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponse {
        private String id;
        private String name;
        private String description;
        private LocalDateTime scheduledTime;
        private int duration;
        private String status;
        private List <String> assignedExaminerId;
        private String location;
}
