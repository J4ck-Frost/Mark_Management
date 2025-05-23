package com.examManagement.ExamAdministrationService.entity;

public enum ExamStatus {
    DRAFT,       // Exam is being prepared
    PUBLISHED,   // Exam is active and visible
    SCORED,      // Exam is in the grading stage.
    COMPLETED,    // The exam is finished
    CANCELED    //Exam is cancel
}
