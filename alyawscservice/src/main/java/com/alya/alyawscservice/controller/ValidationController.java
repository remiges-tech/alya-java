package com.alya.alyawscservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alya.alyawscservice.model.RequestDTO;
import com.alya.alyawscservice.service.AlyaFormatService;

@RestController
@RequestMapping("/api/request")
public class ValidationController {

    @Autowired
    private AlyaFormatService alyaFormatService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateRequest(@RequestBody RequestDTO request) {
        return alyaFormatService.processAndValidateRequest(request);
    }

/**
     * Endpoint to validate and process the request data
     *
     * @param request the request data to process
     * @return a ResponseEntity containing either success or error response
     */
// @PostMapping("/process")
// public String processRequest(@RequestBody RequestDTO requestDto) {
//     // Process the request and format the response
//     String response = AlyaFormatService.processRequest(requestDto);
//     return response;
// }
}
