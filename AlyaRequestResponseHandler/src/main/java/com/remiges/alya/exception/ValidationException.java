package com.remiges.alya.exception;

import java.util.List;

import com.remiges.alya.jsonutil.util.model.ErrorMessage;

public class ValidationException extends RuntimeException {

    private List<ErrorMessage> errorMessages;

    public ValidationException(List<ErrorMessage> errorMessages) {
        super();
        this.errorMessages = errorMessages;
    }

    public List<ErrorMessage> getErrorMessages() {
        return errorMessages;
    }
}

