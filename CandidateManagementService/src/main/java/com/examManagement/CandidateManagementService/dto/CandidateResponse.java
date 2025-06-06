package com.examManagement.CandidateManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponse {
    private String id;
    private String idCard;
    private String fullName;
    private String phoneNumber;
    private String email;
    private boolean gender;
    private Set<String> examIds;
}
