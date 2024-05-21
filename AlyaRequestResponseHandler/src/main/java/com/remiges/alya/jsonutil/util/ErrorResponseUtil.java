package com.remiges.alya.jsonutil.util;

import java.util.Collections;
import java.util.List;

import com.remiges.alya.jsonutil.util.model.ErrorMessage;
import com.remiges.alya.jsonutil.util.model.ErrorResponse;

public final class ErrorResponseUtil {

    private ErrorResponseUtil() {
        // private constructor to prevent instantiation
    }

    /* 
    public static ErrorResponse dataNotFound(String field, Long providedId) {
        List<String> vals = new ArrayList<>();
        vals.add("Required ID: " + field);
        vals.add("Provided ID: " + providedId);
        
        return new ErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage("datanotfound", 404, field, vals))
        );
    }*/
    
    /*This method is implemented for the field that user providing is incorrect, like- id */

    public static ErrorResponse dataNotFound(String field) {
        return new ErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage("datanotfound", 404, field, Collections.emptyList()))
        );
    }

    /*This method is implemented for giving error response as Internal server error */
    public static ErrorResponse internalServerError() {
        return new ErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage("internalservererror", 500, "", Collections.emptyList()))
        );
    }

    public static ErrorResponse unauthorized(String field) {
        return new ErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage("unauthorized", 401, field, Collections.emptyList()))
        );
    }

    public static ErrorResponse badRequest(List<ErrorMessage> errorMessages) {
        return new ErrorResponse(
                "error",
                Collections.emptyMap(),
                errorMessages
        );
    }

    // Add more methods for other types of errors as needed...

}

