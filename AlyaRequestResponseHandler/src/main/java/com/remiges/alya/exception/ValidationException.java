package com.remiges.alya.exception;

import java.util.List;

import com.remiges.alya.jsonutil.util.model.ErrorMessage1;

public class ValidationException extends RuntimeException {

    private List<ErrorMessage1> errorMessages;

    public ValidationException(List<ErrorMessage1> errorMessages) {
        super();
        this.errorMessages = errorMessages;
    }

    public List<ErrorMessage1> getErrorMessages() {
        return errorMessages;
    }
}

