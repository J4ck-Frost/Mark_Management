package com.examManagement.ExamAdministrationService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExaminerRequest {
    @NotBlank (message = "Name cannot be blank")
    private String name;
    @NotBlank (message = "Email cannot be blank")
    @Email (message = "Wrong email format")
    private String email;
    @Pattern(regexp = "\\d{10}")
    private String phoneNumber;
}
