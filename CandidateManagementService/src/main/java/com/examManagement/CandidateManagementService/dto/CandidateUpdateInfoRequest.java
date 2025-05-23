package com.examManagement.CandidateManagementService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateUpdateInfoRequest {
    @Pattern(regexp = "\\d{10}")
    @Size(max = 12, message = "ID card must be at most 12 characters")
    private String idCard;
    @NotBlank(message = "Full name cannot be blank")
    private String fullName;
    @Pattern(regexp = "\\d{10}")
    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;
    @Email(message = "wrong email format")
    @NotBlank (message = "Email cannot be blank")
    private String email;
    private boolean gender;
}
