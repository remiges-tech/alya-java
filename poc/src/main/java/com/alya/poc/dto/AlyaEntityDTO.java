package com.alya.poc.dto;

import com.remiges.alya.annotation.FieldType;
import com.remiges.alya.annotation.ValidField;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlyaEntityDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    @ValidField(message = "Invalid name pattern", fieldType = FieldType.NAME)
    private String name;

    @NotBlank(message = "Email is required")
    @ValidField(message = "Invalid email pattern", fieldType = FieldType.EMAIL)
    private String email;

    @NotBlank(message = "PAN is required")
    @ValidField(message = "Mandatory field", fieldType = FieldType.PAN)
    private String pan;

    @NotBlank(message = "Aadhar is required")
    @ValidField(message = "Mandatory field", fieldType = FieldType.AADHAR)
    private String aadhar;

    @NotBlank(message = "Mobile number is required")
    @ValidField(message = "Mandatory field", fieldType = FieldType.MOBILE_NUMBER)
    private String mobileNumber;

}

