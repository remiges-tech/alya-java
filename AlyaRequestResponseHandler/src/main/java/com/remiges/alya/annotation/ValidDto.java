package com.remiges.alya.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDtoValidator.class)
public @interface ValidDto {
    String message() default "Invalid DTO";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

