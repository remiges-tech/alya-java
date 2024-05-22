package com.remiges.alya.jsonutil.util.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String status;
    private Object data;
    private List<ErrorMessage> messages;

    // Constructors, getters, and setters
 
}
