package com.remiges.alya.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.remiges.alya.dto.RequestDTO;
import com.remiges.alya.dto.RequestParameterDTO;
import com.remiges.alya.jsonutil.util.model.AlyaErrorResponse;
import com.remiges.alya.jsonutil.util.model.AlyaSuccessResponse;
import com.remiges.alya.jsonutil.util.model.DataObject;
import com.remiges.alya.jsonutil.util.model.ErrorMessage;
import com.remiges.alya.jsonutil.util.model.SuccessResponse;
import com.remiges.alya.validation.AlyaValidation;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api")
@Slf4j
public class ValidationController {


    // @PostMapping("/validate")
    // public ResponseEntity<SuccessResponse<String>> validateRequest(@RequestBody RequestParameterDTO request) {
    //     // Validate the request
    //     Map<String, String> errors = AlyaValidation.alyaValidator(request);

    //     // If there are validation errors, combine them into a single message
    //     if (!errors.isEmpty()) {
    //         List<String> errorMessages = List.copyOf(errors.values());
    //         SuccessResponse<String> response = new SuccessResponse<>("error", null, errorMessages);
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    //     }

    //     // If no errors, return success message
    //     SuccessResponse<String> response = new SuccessResponse<>("success", "Request is valid", null);
    //     return ResponseEntity.ok(response);
    // }

      /*
     * Validates the incoming request data.
     *
     * This method accepts a POST request at the `/validate` endpoint. It takes a {@link RequestDTO}
     * object as input, validates it using the {@link AlyaValidation#alyaValidator(RequestDTO)} method,
     * and returns a response indicating the validation result.
     *
     * @param request the request data to validate
     * @return a {@link ResponseEntity} containing either validation errors or a success message
     */

@PostMapping("/validate")
public ResponseEntity<?> validateRequest(@RequestBody RequestDTO request) {
    try {
        Map<String, String> validationErrors = AlyaValidation.alyaValidator(request);

        if (!validationErrors.isEmpty()) {
            List<ErrorMessage> errorMessages = new ArrayList<>();
            for (Map.Entry<String, String> entry : validationErrors.entrySet()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        "invalid_value", // Example error code
                        100,            // Example message code
                        entry.getKey(),
                        List.of(entry.getValue())
                );
                errorMessages.add(errorMessage);
            }
            AlyaErrorResponse errorResponse = new AlyaErrorResponse("error", null, errorMessages);
            log.info("Got the Error for each field");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Create a DataObject instance with the request data
        DataObject data = new DataObject(request.getName(), request.getEmail(), request.getPan(), request.getAadhar(), request.getGst(), 
                                 request.getMobileNumber(),request.getDrivingLicense(), request.getPostalCode(),request.getPostOfficeName(),
                                 request.getPhoneNumber());

        // Return a success response with the data object
        AlyaSuccessResponse successResponse = new AlyaSuccessResponse("success", data, null);
        log.info("Got success as response");
        return ResponseEntity.ok(successResponse);

    } catch (Exception e) {
        // Handle unexpected exceptions
        List<ErrorMessage> errorMessages = new ArrayList<>();
        errorMessages.add(new ErrorMessage("internal_error", 500, null, List.of(e.getMessage())));
        AlyaErrorResponse errorResponse = new AlyaErrorResponse("error", null, errorMessages);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
}

