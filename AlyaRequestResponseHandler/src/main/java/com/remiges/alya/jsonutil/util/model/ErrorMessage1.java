package com.remiges.alya.jsonutil.util.model;

import java.util.List;

import com.remiges.alya.validation.AlyaValidation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage1 {
    private String errcode;
    private int msgcode;
    private String field;
    private List<String> vals;

    public String getValsErrorMessage() {
        return AlyaValidation.combineErrorMessages(null);
    }

}
