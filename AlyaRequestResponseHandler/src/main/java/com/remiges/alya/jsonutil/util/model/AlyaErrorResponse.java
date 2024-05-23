package com.remiges.alya.jsonutil.util.model;


import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlyaErrorResponse {

    public AlyaErrorResponse(String status2, Object data2, List<ErrorMessage> errorMessages) {
        //TODO Auto-generated constructor stub
    }

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private Map<String, Object> data;

    @JsonProperty("messages")
    private List<ErrorMessage> messages;


}

