package com.remiges.alya.json;

import java.util.Collections;
import java.util.List;
import com.remiges.alya.model.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlyaSuccessResponse {

    private String status;
    private RequestDTO data;
    private List<String> messages;

     // Static method to create SuccessResponse
     public static AlyaSuccessResponse success(String status, RequestDTO data ) {
        return new AlyaSuccessResponse("success", data, Collections.emptyList());
    }

}

