package com.remiges.alya.jsonutil.util.model;

import java.util.List;

import com.remiges.alya.validation.AlyaValidation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponse<T> {

    private String status;
    private T data;
    private List<String> messages;

    public String getMessage() {
        return AlyaValidation.combineErrorMessages(null);
    }

    // Using lombok dependency for Constructors, getters, and setters 
    //<T> typically represents a generic type parameter
}
