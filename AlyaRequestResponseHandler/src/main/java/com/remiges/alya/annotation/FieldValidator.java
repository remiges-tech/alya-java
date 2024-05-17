package com.remiges.alya.annotation;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class FieldValidator implements ConstraintValidator<ValidField, String> {

    private FieldType type;

    @Override
    public void initialize(ValidField constraintAnnotation) {
        this.type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Implement validation logic based on the FieldType
        // For example:
        switch (type) {
            case PAN:
                return value != null && value.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}");
            case AADHAR:
                return value != null && value.matches("\\d{12}");
            case GST:
                return value != null && value.matches("\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}\\d[Z]{1}\\d{1}");
            case MOBILE_NUMBER:
                return value != null && value.matches("\\d{10}");
            default:
                return false;
        }
    }
}

