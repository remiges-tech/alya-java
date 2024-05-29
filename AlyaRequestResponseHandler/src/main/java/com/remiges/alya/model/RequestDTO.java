package com.remiges.alya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDTO{

    private String name;
    private String email;
    private String pan;
    private String aadhar;
    private String gst;
    private String mobileNumber;
    private String drivingLicense;
    private String postalCode;
    private String postOfficeName;
    private String phoneNumber;
    private String password;
    private String url;
    private String cardNumber;
    private String vin;
    private String passport;

}
// This is an example to give DTO as request.

