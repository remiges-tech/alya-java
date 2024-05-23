package com.remiges.alya.jsonutil.util;

import java.util.Collections;
import java.util.List;

import com.remiges.alya.jsonutil.util.model.ErrorMessage1;
import com.remiges.alya.jsonutil.util.model.ErrorResponse;

public final class ErrorResponseUtil {

    private ErrorResponseUtil() {
        // private constructor to prevent instantiation
    }

    
    /*This method is implemented for the field that user providing is incorrect, like- id */

    public static ErrorResponse dataNotFound(String field) {
        return new ErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage1("datanotfound", 404, field, Collections.emptyList()))
        );
    }

    /*This method is implemented for giving error response as Internal server error */
    public static ErrorResponse internalServerError() {
        return new ErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage1("internalservererror", 500, "", Collections.emptyList()))
        );
    }

    public static ErrorResponse unauthorized(String field) {
        return new ErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage1("unauthorized", 401, field, Collections.emptyList()))
        );
    }

    public static ErrorResponse badRequest(List<ErrorMessage1> errorMessages) {
        return new ErrorResponse(
                "error",
                Collections.emptyMap(),
                errorMessages
        );
    }

    // Add more methods for other types of errors as needed...

}

