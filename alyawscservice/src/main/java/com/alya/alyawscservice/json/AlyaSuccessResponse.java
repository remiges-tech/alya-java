package com.alya.alyawscservice.json;

import java.util.Collections;
import java.util.List;

import com.alya.alyawscservice.model.DataObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlyaSuccessResponse {

    private String status;
    private DataObject data;
    private List<String> messages;

     // Static method to create SuccessResponse
     public static AlyaSuccessResponse success(DataObject data) {
        return new AlyaSuccessResponse("success", data, Collections.emptyList());
    }
}
