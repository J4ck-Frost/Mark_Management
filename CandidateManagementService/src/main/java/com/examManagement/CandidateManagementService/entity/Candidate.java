package com.examManagement.CandidateManagementService.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document (collection = "candidates")
public class Candidate {
    @Id
    private String id;
    @Indexed(unique = true)
    @Pattern(regexp = "\\d{10}")
    @Size(max = 12, message = "ID card must be at most 12 characters")
    private String idCard;
    private String fullName;
    @Pattern(regexp = "\\d{10}")
    private String phoneNumber;
    @Email
    private String email;
    private boolean gender;
    private List<String> examIds = new ArrayList<>();
}
