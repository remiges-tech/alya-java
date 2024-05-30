package com.remiges.alya.model;

import com.remiges.alya.annotation.ValidField;
import com.remiges.alya.constant.ValidationConstant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDTO{

    
    @ValidField(regex = ValidationConstant.NAME_REGEX, message = "Name must contain only alphabets")
    private String name;

    @ValidField(regex = ValidationConstant.EMAIL_REGEX, message = "Invalid email format")
    private String email;

    @ValidField(regex = ValidationConstant.PAN_REGEX, message = "Invalid PAN format")
    private String pan;

    @ValidField(regex = ValidationConstant.AADHAR_REGEX, message = "Invalid Aadhar number format")
    private String aadhar;

    @ValidField(regex = ValidationConstant.GST_REGEX, message = "Invalid GST format")
    private String gst;

    @ValidField(regex = "\\d{10}", message = "Invalid mobile number format")
    private String mobileNumber;

    @ValidField(regex = ValidationConstant.DRIVING_LICENSE_REGEX, message = "Invalid driving license format")
    private String drivingLicense;

    @ValidField(regex = ValidationConstant.POSTAL_CODE_REGEX, message = "Invalid postal code format")
    private String postalCode;

    @ValidField(regex = ValidationConstant.POST_OFFICE_NAME_REGEX, message = "Invalid post office name format")
    private String postOfficeName;

    @ValidField(regex = ValidationConstant.PASSWORD_PATTERN, message = "Password must contain at least one digit, one lowercase and one uppercase letter, and at least 8 characters")
    private String password;

    @Pattern(regexp = ValidationConstant.URL_PATTERN, message = "Invalid URL format")
    private String url;

    @ValidField(regex = ValidationConstant.CARD_NUMBER_PATTERN, message = "Invalid card number format")
    private String cardNumber;

    @ValidField(regex = ValidationConstant.PASSPORT_NUMBER_PATTERN, message = "Invalid passport format")
    private String passport;


}
// This is an example to give DTO as request.

