package com.remiges.alya.json;

import java.util.Collections;
import java.util.List;
import com.remiges.alya.model.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a success response in the Alya system.
 * This class encapsulates success status, data, and messages for returning structured success responses.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlyaSuccessResponse {

    private String status;
    private RequestDTO data;
    private List<String> messages;

     /**
     * Static factory method to create a success response with default empty messages list.
     *
     * @param status The status of the success response.
     * @param data   The data associated with the success response.
     * @return The AlyaSuccessResponse instance.
     */
    public static AlyaSuccessResponse success(String status, RequestDTO data ) {
        return new AlyaSuccessResponse("success", data, Collections.emptyList());
    }

}

