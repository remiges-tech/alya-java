package com.remiges.alya.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldValidator.class)
@Documented
public @interface ValidField {
    String message() default "Invalid field value";
    String regex();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

