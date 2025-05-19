package com.examManagement.ExamAdministrationService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "exams")
public class Exam {
    @Id
    private String id;
    private String name;
    private String description;
    private LocalDateTime scheduledTime;
    private int duration;
    private List<String> assignedExaminerId;
    @Field(targetType = FieldType.STRING)
    private ExamStatus status = ExamStatus.DRAFT;
    private String location;
}
