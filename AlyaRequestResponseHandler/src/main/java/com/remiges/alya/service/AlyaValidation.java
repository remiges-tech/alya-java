package com.remiges.alya.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.remiges.alya.constant.ValidationConstant;
import com.remiges.alya.model.RequestDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AlyaValidation {

    @Autowired
    private static Validator validator;
    
    public AlyaValidation() {
        try {
            this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        } catch (Exception e) {
        // Log the exception or handle it appropriately
        throw new RuntimeException("Error initializing validator", e);
    }
}

/**
 * Validates the fields in the provided request DTO.
 *
 * @param request the request DTO containing fields to validate
 * @return a map where the keys are field names and the values are error messages
 */
public static Map<String, String> alyaValidator(RequestDTO request) {
    Map<String, String> errors = new HashMap<>();
    
    Set<ConstraintViolation<RequestDTO>> violations = validator.validate(request);
    for (ConstraintViolation<RequestDTO> violation : violations) {
        errors.put(violation.getPropertyPath().toString(), violation.getMessage());
    }
    
    return errors;
}

}

