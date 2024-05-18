package com.remiges.alya.annotation;

import java.util.List;
import java.util.regex.Pattern;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.remiges.alya.constant.ValidationConstant;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidFieldValidator implements ConstraintValidator<ValidField1, String> {

    private List<FieldType> fieldTypes;

    @Override
    public void initialize(ValidField1 constraintAnnotation) {
        this.fieldTypes = List.of(constraintAnnotation.type());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        // Validate each field type
        for (FieldType fieldType : fieldTypes) {
            if (!isValidField(value, fieldType)) {
                return false; // If any field is invalid, return false
            }
        }

        return true; // If all fields are valid, return true
    }

    private boolean isValidField(String value, FieldType fieldType) {
        // Implement validation logic for each field type
        switch (fieldType) {
            case PAN:
                return isValidPan(value);
            case AADHAR:
                return isValidAadhar(value);
            case GST:
                return isValidGST(value);
            case MOBILE_NUMBER:
                return isValidMobileNumber(value);
            case DRIVING_LICENSE:
                return isValidDrivingLicense(value);
            case POSTAL_CODE:
                return isValidPostalCode(value);
            case POST_OFFICE_NAME:
                return isValidPostOfficeName(value);
            case PHONE_NUMBER:
                return isValidPhoneNumber(value);
            default:
                return false;
        }
    }

    // Implement your validation logic for each field type here

    // Validation for PAN
    private boolean isValidPan(String value) {
        // PAN should be alphanumeric and have a length of 10 characters
        // First five characters should be upper case alphabets
        // Next four characters should be digits
        // Last character should be an upper case alphabet
        String panRegex = ValidationConstant.PAN_REGEX;
        return value.matches(panRegex);
    }
    
    // Validation for AADHAR
    private boolean isValidAadhar(String value) {
        // Aadhar number should be a 12-digit number
        String regex = ValidationConstant.AADHAR_REGEX;
        return value.matches(regex);
    }
    
    // Validation for GST
    private boolean isValidGST(String value) {
        // GST Number regex pattern for India
       String gstRegex = ValidationConstant.GST_REGEX;
       // Compile the regex pattern
        Pattern pattern = Pattern.compile(gstRegex);
        // Use stream API to check if the value matches the regex pattern
        return pattern.matcher(value).matches();
    }
    
    // Validation for MOBILENUMBER
    private boolean isValidMobileNumber(String value) {
        // Implement Mobile Number validation logic
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber number = phoneNumberUtil.parse(value, null); // No country code specified
                return phoneNumberUtil.isValidNumber(number);
            } catch (NumberParseException e) {
                System.out.println("NumberParseException was thrown: " + e.toString());
                return false;
        }
    }

    // Validation for DRIVINGLICENSE
    private boolean isValidDrivingLicense(String value) {
        // Regex pattern for driving license validation
        String dlRegex = ValidationConstant.DL_REGEX;
        // Check if the value matches the regex pattern
        return value.matches(dlRegex);
    }
    
    // Validation for POSTALCODE
    private boolean isValidPostalCode(String value) {
        // Postal code regex pattern for India (PIN code)
        String postalCodeRegex = ValidationConstant.POSTAL_CODE_REGEX; // 6 digits, first digit cannot be 0
    
        return value.matches(postalCodeRegex);
    }
    
    // Validation for POSTOFFICENAME
    private boolean isValidPostOfficeName(String value) {
        // Post Office Name should not be null and should contain only letters, spaces, and hyphens
        return value != null && value.matches(ValidationConstant.POST_OFFICE_NAME);
    }
    

    public static boolean isValidPhoneNumber(String value) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(value, null); // No country code specified
            return phoneNumberUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            System.out.println("NumberParseException was thrown: " + e.toString());
            return false;
        }
    }
}


