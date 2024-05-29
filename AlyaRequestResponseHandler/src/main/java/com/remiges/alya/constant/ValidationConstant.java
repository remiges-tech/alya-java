package com.remiges.alya.constant;

public class ValidationConstant {

 // Regex patterns for validation
public static final String NAME_REGEX = "^[A-Za-z\\s-]+$";
public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
public static final String PAN_REGEX = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
public static final String AADHAR_REGEX = "\\d{12}";
public static final String GST_REGEX = "\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}\\d[Z]{1}\\d{1}";
public static final String DRIVING_LICENSE_REGEX = "^[A-Z]{2}-\\d+$";
public static final String POSTAL_CODE_REGEX = "^[1-9][0-9]{5}$";
public static final String POST_OFFICE_NAME_REGEX = "^[A-Za-z\\s-]+$"; 
public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
public static final String URL_PATTERN = "^((https?|ftp|smtp):\\/\\/)?(www\\.)?([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*\\.)+[a-zA-Z]{2,}$";
public static final String CARD_NUMBER_PATTERN = "^\\d{16}$";
public static final String ALL_CARD_TYPE_PATTERN = "^4[0-9]{16}$";
public static final String VIN_PATTERN = "^[A-HJ-NPR-Z\\d]{17}$";
public static final String CURRENCY_AMOUNT_PATTERN = "^\\$?\\d+(\\.\\d{1,2})?$";
public static final String PASSPORT_NUMBER_PATTERN = "^[A-Za-z0-9]{6,20}$";
public static final String HEX_COLOR_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";


}

