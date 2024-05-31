package com.remiges.alya.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.remiges.alya.json.AlyaErrorResponse;
import com.remiges.alya.json.AlyaSuccessResponse;
import com.remiges.alya.json.ErrorMessage;
import com.remiges.alya.model.RequestDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AlyaFormatService {

    // public static String processRequest(RequestDTO request) {
    //     // Validate the request
    //     Map<String, String> errorMessages = AlyaValidation.alyaValidator(request);

    //     // If there are validation errors, return the error response
    //     if (!errorMessages.isEmpty()) {
    //         return AlyaValidation.combineErrorMessages(errorMessages);
    //     }

    //     // If the request is valid, continue with processing
    //     // Your logic for request processing here
    //     // For example, save the request data to a database
    //     // Alternatively, call other services to handle the request

    //     // Return success message
    //     return "Request processed successfully";
    // }

    // public String formatResponse(Object responseData, String status, List<ErrorMessage> errorMessages) {
    //     if ("success".equals(status)) {
    //         AlyaSuccessResponse successResponse = new AlyaSuccessResponse(status, responseData, null);
    //         return successResponse.toString();
    //     } else if ("error".equals(status)) {
    //         AlyaErrorResponse errorResponse = new AlyaErrorResponse(status, null, errorMessages);
    //         return errorResponse.toString();
    //     } else {
    //         return null;
    //     }
    // }

 /**
 * Processes and validates the request.
 *
 * @param request the request data to process
 * @return a ResponseEntity containing either success or error response
 */
public ResponseEntity<?> processAndValidateRequest(RequestDTO request) {
    try {
        Map<String, String> validationErrors = AlyaValidation.alyaValidator(request);

        if (!validationErrors.isEmpty()) {
            List<ErrorMessage> errorMessages = validationErrors.entrySet().stream()
                    .map(entry -> new ErrorMessage("invalid_value", 100, entry.getKey(), List.of(entry.getValue())))
                    .collect(Collectors.toList());
            AlyaErrorResponse errorResponse = AlyaErrorResponse.badRequest(errorMessages);
            log.info("Got the Error for each field");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        RequestDTO data = new RequestDTO(
            request.getName(), request.getEmail(), request.getPan(), request.getAadhar(), request.getGst(), request.getMobileNumber(),
            request.getDrivingLicense(), request.getPostalCode(), request.getPostOfficeName(), request.getPassword(), request.getUrl(), 
            request.getCardNumber(), request.getPassport());

        AlyaSuccessResponse successResponse = AlyaSuccessResponse.success(null, data);
        log.info("Got success as response");
        return ResponseEntity.ok(successResponse);

    } catch (Exception e) {
        AlyaErrorResponse errorResponse = AlyaErrorResponse.internalServerError();
        log.error("Internal server error occurred: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

}


