package com.examManagement.ExamAdministrationService.Mapper;

import com.examManagement.ExamAdministrationService.dto.ExamRequest;
import com.examManagement.ExamAdministrationService.dto.ExamResponse;
import com.examManagement.ExamAdministrationService.entity.Exam;
import com.examManagement.ExamAdministrationService.entity.ExamStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ExamMapper {


        @Mapping(target = "status", ignore = true) // We'll handle status separately in toResponse
        @Mapping(target = "id", ignore = true)
        Exam toEntity(ExamRequest request);

        @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
        ExamResponse toResponse(Exam exam);

        @Named("statusToString")
        default String statusToString(ExamStatus status) {
            return status != null ? status.toString() : null;
        }
}
