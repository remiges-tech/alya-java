package com.remiges.alya.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldValidator.class)
public @interface ValidField {

    String message() default "Invalid field value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    FieldType type();
}

/*
    @Target({ElementType.FIELD}): This annotation specifies that the custom annotation 
    @ValidField can only be applied to fields.

    @Retention(RetentionPolicy.RUNTIME): This annotation indicates that the custom annotation's
     metadata will be available at runtime, which is necessary for validation.

     @Constraint(validatedBy = FieldValidator.class): This annotation specifies the validator class (FieldValidator.class)
      that will perform the validation logic for the @ValidField annotation.

      String message() default "Invalid field value";: This attribute defines 
      the default error message that will be used if the validation fails.

      Class<?>[] groups() default {};: This attribute allows you to specify validation groups,
       which can be used to group related constraints.

       Class<? extends Payload>[] payload() default {};: This attribute allows additional 
       data to be attached to the constraint declaration.

       FieldType type();: This attribute defines a custom attribute type of the FieldType enum. 
       It indicates the type of validation that should be performed for the annotated field.
        */


