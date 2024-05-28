package com.remiges.alya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.remiges.alya.model.RequestDTO;
import com.remiges.alya.service.AlyaFormatService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller class for validating requests.
 */
@RestController
@RequestMapping("/api/request")
@Slf4j
public class ValidationController {

    private final AlyaFormatService alyaFormatService;

    @Autowired
    public ValidationController(AlyaFormatService alyaFormatService) {
        this.alyaFormatService = alyaFormatService;
    }

    /**
     * Validates the request DTO.
     *
     * @param request the request DTO to validate
     * @return a ResponseEntity containing either success or error response
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateRequest(@RequestBody RequestDTO request) {
        log.info("Processing and validating the request");
        return alyaFormatService.processAndValidateRequest(request);
    }
}
