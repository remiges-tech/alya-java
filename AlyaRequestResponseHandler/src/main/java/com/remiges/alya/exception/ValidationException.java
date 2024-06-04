package com.remiges.alya.exception;

/**
 * Custom exception class for validation failure scenarios.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
