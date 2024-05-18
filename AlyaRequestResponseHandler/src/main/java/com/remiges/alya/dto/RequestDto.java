package com.remiges.alya.dto;

import com.remiges.alya.annotation.FieldType;
import com.remiges.alya.annotation.ValidDto;
import com.remiges.alya.annotation.ValidField1;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidDto
public class RequestDto {

    private Long userId;
    
@NotNull
@ValidField1(type = FieldType.PAN)
private String pan;

@NotNull
@ValidField1(type = FieldType.AADHAR)
private String aadhar;

@NotNull
@ValidField1(type = FieldType.GST)
private String gst;

@NotNull
@ValidField1(type = FieldType.MOBILE_NUMBER)
private String mobileNumber;

@NotNull
@ValidField1(type = FieldType.DRIVING_LICENSE)
private String drivingLicense;

@NotNull
@ValidField1(type = FieldType.POSTAL_CODE)
private String postalCode;

@NotNull
@ValidField1(type = FieldType.POST_OFFICE_NAME)
private String postOfficeName;

@NotNull
@ValidField1(type = FieldType.PHONE_NUMBER)
private String phoneNumber;

}

