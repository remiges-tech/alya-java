package com.remiges.alya.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.remiges.alya.model.RequestDTO;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Service class for performing validation on request DTOs in the Alya system.
 * This class provides methods for validating request DTO fields using Bean Validation API.
 */
@Service
public class AlyaValidation {

    @Autowired
    private static Validator validator;
    
     /**
     * Initializes the validator instance using the default validator factory.
     */
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
public static <T> Map<String, String> alyaValidator(T obj) {
    Map<String, String> errors = new HashMap<>();
    
    Set<ConstraintViolation<T>> violations = validator.validate(obj);
    for (ConstraintViolation<T> violation : violations) {
        errors.put(violation.getPropertyPath().toString(), violation.getMessage());
    }
    
    return errors;
}

}

