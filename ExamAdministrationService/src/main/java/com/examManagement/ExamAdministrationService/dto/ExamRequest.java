package com.examManagement.ExamAdministrationService.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ExamRequest {
    @NotBlank(message = "Exam name cannot be blank")
    private String name;
    private String description;
    @NotNull(message = "Scheduled time cannot be null")
    @FutureOrPresent(message = "Scheduled time must be in the future or present")
    private LocalDateTime scheduledTime;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration must be positive")
    private Integer duration;
    @NotBlank(message = "Location cannot be blank")
    private String location;

    @NotNull(message = "Examiner IDs list cannot be null")
    @NotEmpty(message = "Examiner IDs list cannot be empty")
    private List<
            @NotBlank(message = "Examiner ID cannot be blank")
                    String
            > assignedExaminerId;
}
