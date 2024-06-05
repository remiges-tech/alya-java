package com.remiges.alya.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.remiges.alya.model.RequestDTO;
import com.remiges.alya.service.AlyaFormatService;

@RestController
@RequestMapping("/api/request")
@Validated
public class ExampleController {

        @Autowired
        private AlyaFormatService alyaFormatService;

        @PostMapping("/validate")
        public ResponseEntity<?> validateRequest(@RequestBody RequestDTO request) {
            return alyaFormatService.processAndValidateRequest(request);

        }

}
