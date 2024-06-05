package com.alya.poc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.alya.poc.model.AlyaEntity;
import com.alya.poc.service.PocService;
import com.remiges.alya.json.AlyaErrorResponse;
import com.remiges.alya.json.ErrorMessage;
import com.remiges.alya.validation.AlyaValidation;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private PocService pocService;


    @GetMapping
    public List<AlyaEntity> getAllUsers() {
        return pocService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlyaEntity> getUserById(@PathVariable Long id) {
        Optional<AlyaEntity> user = pocService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody AlyaEntity user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ErrorMessage> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(this::toErrorMessage)
                    .collect(Collectors.toList());
            AlyaErrorResponse errorResponse = AlyaErrorResponse.badRequest(errorMessages);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        AlyaEntity createdUser = pocService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody AlyaEntity userDetails, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ErrorMessage> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(this::toErrorMessage)
                    .collect(Collectors.toList());
            AlyaErrorResponse errorResponse = AlyaErrorResponse.badRequest(errorMessages);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        Optional<AlyaEntity> updatedUser = pocService.updateUser(id, userDetails);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        pocService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private ErrorMessage toErrorMessage(FieldError fieldError) {
        return new ErrorMessage(
                "InvalidField",
                HttpStatus.BAD_REQUEST.value(),
                fieldError.getField(),
                List.of(fieldError.getDefaultMessage())
        );
    }
}

