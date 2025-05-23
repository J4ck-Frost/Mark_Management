package com.examManagement.CandidateManagementService.mapper;

import com.examManagement.CandidateManagementService.dto.CandidateRequest;
import com.examManagement.CandidateManagementService.dto.CandidateResponse;
import com.examManagement.CandidateManagementService.entity.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidateMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "examIds", ignore = true)
    Candidate toEntity(CandidateRequest request);

    CandidateResponse toResponse(Candidate candidate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "examIds", ignore = true)
    void updateFromRequest(@MappingTarget Candidate candidate, CandidateRequest request);


}
