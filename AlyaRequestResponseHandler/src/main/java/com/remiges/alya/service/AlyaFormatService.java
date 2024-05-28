package com.remiges.alya.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.remiges.alya.json.AlyaErrorResponse;
import com.remiges.alya.json.AlyaSuccessResponse;
import com.remiges.alya.json.ErrorMessage;
import com.remiges.alya.model.DataObject;
import com.remiges.alya.model.RequestDTO;
import com.remiges.alya.validation.AlyaValidation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for processing and validating requests.
 */
@Slf4j
@Service
public class AlyaFormatService {   

    /**
     * Processes and validates the request.
     *
     * @param request the request data to process
     * @return a ResponseEntity containing either success or error response
     */
    public ResponseEntity<?> processAndValidateRequest(RequestDTO request) {
        try {
            // Validate the request data
            Map<String, String> validationErrors = AlyaValidation.alyaValidator(request);

            // If validation errors exist, construct a bad request error response
            if (!validationErrors.isEmpty()) {
                List<ErrorMessage> errorMessages = validationErrors.entrySet().stream()
                        .map(entry -> new ErrorMessage("invalid_value", 100, entry.getKey(), List.of(entry.getValue())))
                        .collect(Collectors.toList());
                AlyaErrorResponse errorResponse = AlyaErrorResponse.badRequest(errorMessages);
                log.info("Validation errors found: {}", errorMessages);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // If no validation errors, create a DataObject instance with the request data
            DataObject data = new DataObject(
                request.getName(), request.getEmail(), request.getPan(), request.getAadhar(), request.getGst(), request.getMobileNumber(),
                request.getDrivingLicense(), request.getPostalCode(), request.getPostOfficeName(), request.getPhoneNumber());

            // Return a success response with the data object
            AlyaSuccessResponse successResponse = AlyaSuccessResponse.success(data);
            log.info("Request processed successfully");
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            // Handle unexpected exceptions and construct an internal server error response
            AlyaErrorResponse errorResponse = AlyaErrorResponse.internalServerError();
            log.error("Internal server error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}



