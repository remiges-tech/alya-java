package com.remiges.alya.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

}

