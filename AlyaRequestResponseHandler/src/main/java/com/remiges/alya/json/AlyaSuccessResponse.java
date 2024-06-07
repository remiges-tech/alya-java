package com.remiges.alya.json;

import java.util.ArrayList;
import java.util.List;

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
public class AlyaSuccessResponse<T> {

    private String status;
    private T data;
    private List<String> messages;

   /**
 * Creates a new {@code AlyaSuccessResponse} instance with the provided status and data.
 *
 * @param <T>   the type of the data to be included in the success response
 * @param status the status message to be included in the success response
 * @param data   the data to be included in the success response
 * @return       a new {@code AlyaSuccessResponse} instance containing the provided status and data
 */
public static <T> AlyaSuccessResponse success(String status, T data) {
    return new AlyaSuccessResponse("success", data, new ArrayList<String>());
}


    

}

