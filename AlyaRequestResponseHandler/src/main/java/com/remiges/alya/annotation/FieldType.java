package com.remiges.alya.annotation;

/**
 * Enum representing various types of fields for validation.
 * This enum is used in conjunction with the {@link ValidField} annotation
 * to specify the type of validation to be performed on DTO fields.
 */
public enum FieldType {
    
    NAME,
    EMAIL,
    PAN,
    AADHAR,
    MOBILE_NUMBER,
    GST,
    DRIVING_LICENSE,
    POSTAL_CODE,
    POST_OFFICE_NAME,
    PASSWORD,
    URL,
    CARD_NUMBER,
    ALL_CARD_TYPE,
    VIN,
    CURRENCY_AMOUNT,
    PASSPORT_NUMBER,
    HEX_COLOR;

}
