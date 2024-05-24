package com.remiges.alya.jobs;

public enum ErrorCodes {

    NOERROR(0, ""),
    ERROR(-1, "");

    Integer error;
    String description;

    ErrorCodes(Integer error, String value) {
        this.error = error;
        this.description = value;
    }

}
