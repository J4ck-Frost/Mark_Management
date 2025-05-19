package com.examManagement.ExamAdministrationService.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExaminerResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
}
