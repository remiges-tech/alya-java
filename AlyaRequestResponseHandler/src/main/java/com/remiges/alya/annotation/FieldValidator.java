package com.remiges.alya.annotation;

import org.springframework.stereotype.Component;

import com.remiges.alya.constant.ValidationConstant;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/*This class is implemented for the validation of DTO field using Reg-ex pattern. */

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
                return value != null && value.matches(ValidationConstant.PAN_REGEX);
            case AADHAR:
                return value != null && value.matches(ValidationConstant.AADHAR_REGEX);
            case GST:
                return value != null && value.matches(ValidationConstant.GST_REGEX);
            case MOBILE_NUMBER:
                return value != null && value.matches(ValidationConstant.MOBILE_NUMBER_REGEX);
            case DRIVING_LICENSE:
                return value != null && value.matches(ValidationConstant.DL_REGEX);
            case POSTAL_CODE:
                return value != null && value.matches(ValidationConstant.POSTAL_CODE_REGEX);
            case POST_OFFICE_NAME:
                return value != null && value.matches(ValidationConstant.POST_OFFICE_NAME);
            case PHONE_NUMBER:
                return value != null && value.matches(ValidationConstant.PHONE_NUMBER);    
            default:
                return false;
        }
    }
}

