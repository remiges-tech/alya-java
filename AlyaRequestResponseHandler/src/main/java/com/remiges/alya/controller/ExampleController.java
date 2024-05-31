package com.remiges.alya.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.remiges.alya.model.RequestDTO;
import com.remiges.alya.service.AlyaFormatService;
import com.remiges.alya.service.AlyaValidation;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@RestController
@RequestMapping("/api/request")
@Validated
@Slf4j
public class ExampleController {

        @Autowired
        private AlyaValidation alyaValidation;

        @Autowired
        private AlyaFormatService alyaFormatService;

        @PostMapping("/validate")
        public ResponseEntity<?> validateRequest(@RequestBody RequestDTO request) {
            return alyaFormatService.processAndValidateRequest(request);

        }

}
