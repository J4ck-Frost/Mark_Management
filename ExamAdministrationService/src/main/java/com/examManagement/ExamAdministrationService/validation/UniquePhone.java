package com.examManagement.ExamAdministrationService.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniquePhoneValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniquePhone {
    String message() default "Phone number is already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String idField() default "id";
}
