package com.remiges.alya.constant;

import javax.validation.constraints.NotNull;

import com.remiges.alya.annotation.FieldType;

import lombok.Data;

@Data
public class FieldValidationRequest {

    @NotNull
    private FieldType type;

    @NotNull
    private String value;

    private FieldType fieldType;
    private String[] values;

    public int getFieldType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFieldType'");
    }

    public String[] getValues() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getValues'");
    }

    // Getters and setters
}
