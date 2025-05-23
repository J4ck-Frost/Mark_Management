package com.examManagement.ExamAdministrationService.Mapper;

import com.examManagement.ExamAdministrationService.dto.ExaminerRequest;
import com.examManagement.ExamAdministrationService.dto.ExaminerResponse;
import com.examManagement.ExamAdministrationService.entity.Examiner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExaminerMapper {
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "id", ignore = true)
    Examiner toEntity(ExaminerRequest request);

    ExaminerResponse toResponse(Examiner examiner);
}
