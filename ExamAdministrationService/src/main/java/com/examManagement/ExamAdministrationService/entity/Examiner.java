package com.examManagement.ExamAdministrationService.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "examiners")
public class Examiner {
    @Id
    private String id;
    private String name;
    @Email
    private String email;
    @Pattern(regexp = "\\d{10}")
    private String phoneNumber;
    private boolean isActive = true;
}
