package com.remiges.alya.service;

import com.remiges.alya.dto.RequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class ValidationService1 {

    private Validator validator;

    public ValidationService1() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public Set<ConstraintViolation<RequestDto>> validateDto(RequestDto dto) {
        return validator.validate(dto);
    }

    public boolean isDtoValid(RequestDto dto) {
        return validateDto(dto).isEmpty();
    }

    public Set<ConstraintViolation<RequestDto>> validateField(RequestDto dto, String fieldName) {
        return validator.validateProperty(dto, fieldName);
    }

    public boolean isFieldValid(RequestDto dto, String fieldName) {
        return validateField(dto, fieldName).isEmpty();
    }

    public Set<ConstraintViolation<RequestDto>> validateClassLevel(RequestDto dto) {
        return validator.validate(dto); 
    }

    public boolean isClassLevelValid(RequestDto dto) {
        return validateClassLevel(dto).isEmpty();
    }
}


