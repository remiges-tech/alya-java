package com.remiges.alya.constant;

public class ValidationConstant {

 // Regex patterns for validation
public static final String NAME_REGEX = "^[A-Za-z\\s-]+$";
public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
public static final String PAN_REGEX = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
public static final String AADHAR_REGEX = "\\d{12}";
public static final String GST_REGEX = "\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}\\d[Z]{1}\\d{1}";
public static final String MOBILE_NUMBER_REGEX = "\\d{10}"; 
public static final String DRIVING_LICENSE_REGEX = "^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$";
public static final String POSTAL_CODE_REGEX = "^[1-9][0-9]{5}$";
public static final String POST_OFFICE_NAME_REGEX = "^[A-Za-z\\s-]+$"; 
public static final String PHONE_NUMBER_REGEX = "\\d{10}"; 

}

