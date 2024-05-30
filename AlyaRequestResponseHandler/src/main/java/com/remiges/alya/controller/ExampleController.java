package com.remiges.alya.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.remiges.alya.model.RequestDTO;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/request")
@Validated
public class ExampleController {

    @PostMapping("/validate")
    public ResponseEntity<RequestDTO> validateRequest(@RequestBody @Valid RequestDTO requestDTO) {
        // If validation passes, return a success response
        return ResponseEntity.ok(requestDTO);
    }
}
