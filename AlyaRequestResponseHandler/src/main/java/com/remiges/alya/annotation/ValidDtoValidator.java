package com.remiges.alya.annotation;


import com.remiges.alya.dto.RequestDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDtoValidator implements ConstraintValidator<ValidDto, RequestDto> {

    @Override
    public void initialize(ValidDto constraintAnnotation) {
    }

    @Override
    public boolean isValid(RequestDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
    
        // Check if required fields are not null
        if (value.getPan() == null || value.getAadhar() == null || value.getGst() == null ||
                value.getMobileNumber() == null || value.getDrivingLicense() == null ||
                value.getPostalCode() == null || value.getPostOfficeName() == null ||
                value.getPhoneNumber() == null) {
            return false; // If any required field is null, the DTO is invalid
        }
    
        // Optionally, you can add more validation logic here
        
        return true; // If all checks pass, the DTO is valid
    }
    
}


