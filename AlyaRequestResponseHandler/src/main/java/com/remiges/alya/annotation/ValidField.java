package com.remiges.alya.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation for validating DTO fields based on their type.
 * This annotation is applied to fields within DTO classes to specify the type of validation
 * to be performed on those fields.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldValidator.class)
@Documented
public @interface ValidField {

    /**
     * Specifies the error message when validation fails.
     * 
     * @return The error message.
     */
    String message() default "Invalid field value";

    /**
     * Specifies the validation groups.
     * 
     * @return The validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Specifies the payload associated with the validation.
     * 
     * @return The payload.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Specifies the type of the field.
     * 
     * @return The type of the field.
     */
    FieldType fieldType();
}
