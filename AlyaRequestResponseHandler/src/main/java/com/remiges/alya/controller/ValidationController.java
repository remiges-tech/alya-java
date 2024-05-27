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

@RestController
@RequestMapping("/api/request")
@Slf4j
public class ValidationController {

    @Autowired
    private AlyaFormatService alyaFormatService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateRequest(@RequestBody RequestDTO request) {
        log.info("every class, method, logic and other data is working properly");
        return alyaFormatService.processAndValidateRequest(request);
    }

}
// This Class is an example to check wheather my library is working or not.

