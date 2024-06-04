package com.remiges.alya.json;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an error message in the Alya system.
 * This class encapsulates error code, message code, field, and values for returning structured error messages.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    
        private String errcode;
        private int msgcode;
        private String field;
        private List<String> vals;

}

