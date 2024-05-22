package com.remiges.alya.dto;

import com.remiges.alya.annotation.FieldType;
import com.remiges.alya.annotation.ValidDto;
import com.remiges.alya.annotation.ValidField;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidDto
public class RequestParameterDTO {

private Long userId;

private String name;

private String email;

@NotNull
@ValidField(type = FieldType.PAN)
private String pan;

@NotNull
@ValidField(type = FieldType.AADHAR)
private String aadhar;

@NotNull
@ValidField(type = FieldType.GST)
private String gst;

@NotNull
@ValidField(type = FieldType.MOBILE_NUMBER)
private String mobileNumber;

@NotNull
@ValidField(type = FieldType.DRIVING_LICENSE)
private String drivingLicense;

@NotNull
@ValidField(type = FieldType.POSTAL_CODE)
private String postalCode;

@NotNull
@ValidField(type = FieldType.POST_OFFICE_NAME)
private String postOfficeName;

@NotNull
@ValidField(type = FieldType.PHONE_NUMBER)
private String phoneNumber;

}



