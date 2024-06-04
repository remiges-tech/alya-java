package com.remiges.alya.model;

import com.remiges.alya.annotation.FieldType;
import com.remiges.alya.annotation.ValidField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a data transfer object (DTO) used for making requests in the Alya system.
 * This class contains fields annotated with {@link ValidField} to specify validation rules.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDTO{

    @ValidField(message = "Invalid name", fieldType = FieldType.NAME)
    private String name;

    @ValidField(message = "Invalid email", fieldType = FieldType.EMAIL)
    private String email;

    @ValidField(message = "Invalid PAN format", fieldType = FieldType.PAN)
    private String pan;

    @ValidField(message = "Invalid Aadhar number format", fieldType = FieldType.AADHAR)
    private String aadhar;

    @ValidField(message = "Invalid GST format", fieldType = FieldType.GST)
    private String gst;

    @ValidField(message = "Invalid mobile number format", fieldType = FieldType.MOBILE_NUMBER)
    private String mobileNumber;

    @ValidField(message = "Invalid driving license format", fieldType = FieldType.DRIVING_LICENSE)
    private String drivingLicense;

    @ValidField(message = "Invalid postal code format", fieldType = FieldType.POSTAL_CODE)
    private String postalCode;

    @ValidField(message = "Invalid post office name format", fieldType = FieldType.POST_OFFICE_NAME)
    private String postOfficeName;

    @ValidField(message = "Password must contain at least one digit, one lowercase and one uppercase letter, and at least 8 characters", fieldType = FieldType.PASSWORD)
    private String password;

    @ValidField(message = "check url", fieldType = FieldType.URL)
    private String url;

    @ValidField(message = "Invalid card number format", fieldType = FieldType.CARD_NUMBER)
    private String cardNumber;

    @ValidField(message = "Invalid passport format", fieldType = FieldType.PASSPORT_NUMBER)
    private String passport;

}
// This is an example to give DTO as request.

