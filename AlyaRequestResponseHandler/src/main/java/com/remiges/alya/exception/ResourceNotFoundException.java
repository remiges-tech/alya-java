package com.remiges.alya.exception;

/**
 * Custom exception class for resource not found scenarios.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}


