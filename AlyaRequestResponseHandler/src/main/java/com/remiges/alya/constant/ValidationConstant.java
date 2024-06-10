package com.remiges.alya.constant;

/**
 * Class containing regex patterns for field validation.
 * This class provides constant regex patterns for validating various types of fields,
 * such as names, emails, PAN numbers, Aadhar numbers, etc.
 */

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
 public static final String VISA_CARD = "^4[0-9]{12}(?:[0-9]{3})?$";
 public static final String MASTER_CARD = "^4(?:\\d{12}|\\d{15})$";
 public static final String AMERICAN_STRING_CARD = "^(?:34|37)\\d{13}$\n";
 public static final String DISCOVER_CARD = "^(?:352[89]|35[3-8]\\d)\\d{12,15}$\n";
 public static final String DINNER_CLUB_CARD = "\\b30[0-5]\\d-\\d{6}-\\d{4}\\b\t\n";
 public static final String JCB_CARD = "^(?:352[89]|35[3-8]\\d)\\d{12,15}$\n";
 public static final String ISBN = "^(?:\\d{9}[\\dX]|\\d{10})$\n";
 public static final String IBAN = "^[A-Z]{2}\\d{2}[A-Za-z0-9]{0,30}$";
 public static final String DD = "^(-?\\d{1,2}(?:\\.\\d+)?),\\s*(-?\\d{1,3}(?:\\.\\d+)?)$\n";
 public static final String VIN_PATTERN = "^[A-HJ-NPR-Z\\d]{17}$";
 public static final String CURRENCY_AMOUNT_PATTERN = "^\\$?\\d+(\\.\\d{1,2})?$";
 public static final String PASSPORT_NUMBER_PATTERN = "^[A-Za-z0-9]{6,20}$";
 public static final String HEX_COLOR_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";


}

