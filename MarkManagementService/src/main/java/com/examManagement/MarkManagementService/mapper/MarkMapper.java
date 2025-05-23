package com.examManagement.MarkManagementService.mapper;

import com.examManagement.MarkManagementService.dto.MarkRequest;
import com.examManagement.MarkManagementService.dto.MarkResponse;
import com.examManagement.MarkManagementService.entity.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, ZoneId.class})
public interface MarkMapper {

    @Mapping(target = "registeredAt", expression = "java(LocalDateTime.now(ZoneId.of(\"Asia/Ho_Chi_Minh\"))")
    @Mapping(target = "finalized", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "examId", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "scoredAt", ignore = true)
    Mark toEntity(MarkRequest request);

    MarkResponse toResponse(Mark mark);
}