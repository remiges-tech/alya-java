package com.remiges.alya.controller;


import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.remiges.alya.dto.RequestParameterDTO;
import com.remiges.alya.jsonutil.util.model.SuccessResponse;
import com.remiges.alya.validation.AlyaValidation;


@RestController
@RequestMapping("/api")
public class ValidationController {
    
    // @PostMapping("/validate")
    // public ResponseEntity<?> validateRequest(@RequestBody RequestDTO request) {
    //     // Validate the request
    //     Map<String, String> errors = AlyaValidation.alyaValidator(request);

    //     // If there are validation errors, combine them into a single message
    //     if (!errors.isEmpty()) {
    //         String errorMessage = AlyaValidation.combineErrorMessages(errors);
    //         return ResponseEntity.badRequest().body(errorMessage);
    //     }

    //     // If no errors, return success message
    //     return ResponseEntity.ok("Request is valid");
    // }


    @PostMapping("/validate")
    public ResponseEntity<SuccessResponse<String>> validateRequest(@RequestBody RequestParameterDTO request) {
        // Validate the request
        Map<String, String> errors = AlyaValidation.alyaValidator(request);

        // If there are validation errors, combine them into a single message
        if (!errors.isEmpty()) {
            List<String> errorMessages = List.copyOf(errors.values());
            SuccessResponse<String> response = new SuccessResponse<>("error", null, errorMessages);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // If no errors, return success message
        SuccessResponse<String> response = new SuccessResponse<>("success", "Request is valid", null);
        return ResponseEntity.ok(response);
    }
}

