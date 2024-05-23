package com.remiges.alya.jsonutil.util.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    
        // private String errcode;
        // private int msgcode;
        // private String field;
        // private List<String> vals;

        @JsonProperty("errcode")
        private String errcode;
    
        @JsonProperty("msgcode")
        private int msgcode;
    
        @JsonProperty("field")
        private String field;
    
        @JsonProperty("vals")
        private List<String> vals;

     

          public ErrorMessage(String string, int i, String key, String string2) {
            //TODO Auto-generated constructor stub
        }

    // Additional method to set vals from a Map
    public void setValsFromMap(Map<String, String> validationErrors) {
        this.vals = new ArrayList<>(validationErrors.values());
    }


}

