package com.examManagement.ExamAdministrationService.entity;

public enum ExamStatus {
    DRAFT,       // Exam is being prepared
    PUBLISHED,   // Exam is active and visible
    COMPLETED,    // Exam is finished
    CANCELED    //Exam is cancel
}
