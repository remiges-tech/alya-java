package com.remiges.alya.json;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    
        private String errcode;
        private int msgcode;
        private String field;
        private List<String> vals;

}

