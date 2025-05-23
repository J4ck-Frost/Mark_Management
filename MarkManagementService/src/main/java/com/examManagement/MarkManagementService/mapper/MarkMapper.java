package com.examManagement.MarkManagementService.mapper;

import com.examManagement.MarkManagementService.dto.MarkRequest;
import com.examManagement.MarkManagementService.dto.MarkResponse;
import com.examManagement.MarkManagementService.entity.Mark;

import java.time.LocalDateTime;

public class MarkMapper {
    public static Mark toEntity(MarkRequest request) {
        Mark mark = new Mark();
        mark.setExaminerId(request.getExaminerId());
        mark.setRegisteredAt(LocalDateTime.now());
        mark.setFinalized(false);
        return mark;
    }

    public static MarkResponse toResponse(Mark mark) {
        MarkResponse response = new MarkResponse();
        response.setId(mark.getId());
        response.setCandidateId(mark.getCandidateId());
        response.setExamId(mark.getExamId());
        response.setExaminerId(mark.getExaminerId());
        response.setFinalized(mark.isFinalized());
        response.setScore(mark.getScore());
        response.setRegisteredAt(mark.getRegisteredAt());
        response.setScoredAt(mark.getScoredAt());
        return response;
    }
}
