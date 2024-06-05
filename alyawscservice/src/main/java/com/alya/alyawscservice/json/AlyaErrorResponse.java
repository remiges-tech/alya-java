package com.alya.alyawscservice.json;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlyaErrorResponse {

    private String status;
    private Map<String, Object> data;
    private List<ErrorMessage> messages;


    public static AlyaErrorResponse dataNotFound(String field) {
        return new AlyaErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage("datanotfound", 404, field, Collections.emptyList()))
        );
    }

    /*This method is implemented for giving error response as Internal server error */
    public static AlyaErrorResponse internalServerError() {
        return new AlyaErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage("internalservererror", 500, "", Collections.emptyList()))
        );
    }

    public static AlyaErrorResponse unauthorized(String field) {
        return new AlyaErrorResponse(
                "error",
                Collections.emptyMap(),
                Collections.singletonList(new ErrorMessage("unauthorized", 401, field, Collections.emptyList()))
        );
    }

    public static AlyaErrorResponse badRequest(List<ErrorMessage> errorMessages) {
        return new AlyaErrorResponse(
                "error",
                Collections.emptyMap(),
                errorMessages
        );
    }

    

}
