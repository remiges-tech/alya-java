package com.remiges.alyapoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.remiges.alya.service.AlyaFormatService;
import com.remiges.alyapoc.dto.PocDto;

@RestController
@RequestMapping("/api")
public class PocController {


     @Autowired
     private AlyaFormatService alyaFormatService;

        @PostMapping("/validate")
        public ResponseEntity<?> validateRequest(@RequestBody PocDto request1) {
            return alyaFormatService.processAndValidateRequest(request1);

        }

}

