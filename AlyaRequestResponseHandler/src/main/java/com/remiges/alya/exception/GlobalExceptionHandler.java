package com.remiges.alya.exception;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.remiges.alya.json.AlyaErrorResponse;
import com.remiges.alya.json.ErrorMessage;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException and returns an appropriate error response.
     *
     * @param ex the exception
     * @return a ResponseEntity with an error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<AlyaErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorMessage errorMessage = new ErrorMessage("resource_not_found", 404, ex.getMessage(), Collections.emptyList());
        AlyaErrorResponse errorResponse = new AlyaErrorResponse("error", Collections.emptyMap(), Collections.singletonList(errorMessage));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    /**
     * Handles ValidationException and returns an appropriate error response.
     *
     * @param ex the exception
     * @return a ResponseEntity with an error response
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<AlyaErrorResponse> handleValidationException(ValidationException ex) {
        ErrorMessage errorMessage = new ErrorMessage("field not verified", 404, ex.getMessage(), Collections.emptyList());
        AlyaErrorResponse errorResponse = new AlyaErrorResponse("error", Collections.emptyMap(), Collections.singletonList(errorMessage));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
