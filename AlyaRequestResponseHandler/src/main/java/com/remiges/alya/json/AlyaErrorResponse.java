package com.remiges.alya.json;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an error response in the Alya system.
 * This class encapsulates error status, data, and error messages for returning structured error responses.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlyaErrorResponse {

    private String status;
    private Map<String, Object> data;
    private List<ErrorMessage> messages;

      /**
     * Constructs an AlyaErrorResponse for a data not found error.
     *
     * @param field The field for which data is not found.
     * @return The AlyaErrorResponse instance.
     */
    public static AlyaErrorResponse dataNotFound(String field) {
        return new AlyaErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage("datanotfound", 404, field, Collections.emptyList()))
        );
    }

      /**
     * Constructs an AlyaErrorResponse for an internal server error.
     *
     * @return The AlyaErrorResponse instance.
     */
    public static AlyaErrorResponse internalServerError() {
        return new AlyaErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage("internalservererror", 500, "", Collections.emptyList()))
        );
    }

      /**
     * Constructs an AlyaErrorResponse for an unauthorized access error.
     *
     * @param field The field for which access is unauthorized.
     * @return The AlyaErrorResponse instance.
     */
    public static AlyaErrorResponse unauthorized(String field) {
        return new AlyaErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage("unauthorized", 401, field, Collections.emptyList()))
        );
    }

    /**
     * Constructs an AlyaErrorResponse for a bad request error.
     *
     * @param errorMessages The list of error messages.
     * @return The AlyaErrorResponse instance.
     */
    public static AlyaErrorResponse badRequest(List<ErrorMessage> errorMessages) {
        return new AlyaErrorResponse(
                "error",
                Collections.emptyMap(),
                errorMessages
        );
    }

    

}
