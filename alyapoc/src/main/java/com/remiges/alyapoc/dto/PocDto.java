package com.remiges.alyapoc.dto;

import com.remiges.alya.annotation.FieldType;
import com.remiges.alya.annotation.ValidField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PocDto {

    @ValidField(message = "Invalid Formate", fieldType = FieldType.NAME)
    private String name;

    @ValidField(message = "Invalid Formate", fieldType = FieldType.PAN)
    private String pan;

}
