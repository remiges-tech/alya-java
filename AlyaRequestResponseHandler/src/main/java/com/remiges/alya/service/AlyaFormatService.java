package com.remiges.alya.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.remiges.alya.annotation.FieldValidator;
import com.remiges.alya.annotation.ValidField;
import com.remiges.alya.exception.ResourceNotFoundException;
import com.remiges.alya.json.AlyaErrorResponse;
import com.remiges.alya.json.AlyaSuccessResponse;
import com.remiges.alya.json.ErrorMessage;
import com.remiges.alya.validation.AlyaValidation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for processing and validating requests in the Alya system.
 * This service class provides methods for processing and validating request data,
 * and returning appropriate success or error responses.
 */
@Service
@Slf4j
@Scope("prototype")
public class AlyaFormatService {

 /**
 * Processes and validates the request.
 *
 * @param request the request data to process
 * @return a ResponseEntity containing either success or error response
 */
 public <T> ResponseEntity<?> processAndValidateRequest(@RequestBody T request) {
        try {
            // Create a map to store only non-null fields and their values
            Map<String, Object> nonNullFields = new HashMap<>();

            // Use reflection to iterate over the fields in the DTO
            for (Field field : request.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(request);
                if (value != null) {
                    nonNullFields.put(field.getName(), value);
                }
            }

            // Validate only the non-null fields
            Map<String, String> validationErrors = new HashMap<>();
            for (String fieldName : nonNullFields.keySet()) {
                Field field = request.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                ValidField annotation = field.getAnnotation(ValidField.class);
                if (annotation != null) {
                    FieldValidator validator = new FieldValidator();
                    validator.initialize(annotation);
                    boolean isValid = validator.isValid(nonNullFields.get(fieldName).toString(), null);

                    if (!isValid) {
                        validationErrors.put(fieldName, annotation.message());
                    }
                }
            }

            if (!validationErrors.isEmpty()) {
                List<ErrorMessage> errorMessages = validationErrors.entrySet().stream()
                        .map(entry -> new ErrorMessage("invalid_value", 100, entry.getKey(), List.of(entry.getValue())))
                        .collect(Collectors.toList());
                AlyaErrorResponse errorResponse = AlyaErrorResponse.badRequest(errorMessages);
                log.info("Got the Error for each field");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            AlyaSuccessResponse successResponse = AlyaSuccessResponse.success("success", null);
            log.info("Got success as response");
            return ResponseEntity.ok(successResponse);

        } catch (ResourceNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            AlyaErrorResponse errorResponse = AlyaErrorResponse.internalServerError();
            log.error("Internal server error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}


