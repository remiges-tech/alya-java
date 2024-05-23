package com.remiges.alya.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.remiges.alya.constant.ValidationConstant;
import com.remiges.alya.dto.RequestDTO;
import com.remiges.alya.dto.RequestParameterDTO;

public class AlyaValidation {

    public static Map<String, String> alyaValidator(RequestDTO request) {
        Map<String, String> errors = new HashMap<>();

        // Validate name
        if (!isValidName(request.getName())) {
            errors.put("name", "Name must contain only letters and spaces");
        }

        // Validate email
        if (!isValidEmail(request.getEmail())) {
            errors.put("email", "Email is not valid");
        }

        // Validate PAN
        if (!isValidPAN(request.getPan())) {
            errors.put("pan", "PAN must be in the format ABCDE1234F");
        }

        // Validate Aadhar
        if (!isValidAadhar(request.getAadhar())) {
            errors.put("aadhar", "Aadhar must be a 12-digit number");
        }

        //Validate GST
        if(isValidGST(request.getGst())){
            errors.put("gst", "GST formate is incorrect");
        }

        //Validate Mobile Number
        if(isValidMobileNumber(request.getMobileNumber())){
            errors.put("Mobile Number", "Mobile number is incorrect");
        }

        //Validate Driving License
        if(isValidDrivingLicense(request.getDrivingLicense())){
            errors.put("DL", "DL number is not proper");
        }

        //Validate POSTALCODE
        if(isValidPostalCode(request.getPostalCode())){
            errors.put("Postal Code", "Postal Code is not in correct formate");
        }

        //Validate POSTOFFICENAME
        if(isValidPostOfficeName(request.getPostOfficeName())){
            errors.put("Post Office", "Post Office Name formate is not proper");
        }

        //Validate Phone Number
        if(isValidPhoneNumber(request.getPhoneNumber())){
            errors.put("Phone Number", "Phone Number formate is not correct");
        }

        return errors;
    }

    private static boolean isValidName(String name) {
        // Name should contain only letters and spaces
        return name.matches("[a-zA-Z ]+");
    }

    private static boolean isValidEmail(String email) {
        // Basic email pattern validation as per regex
        return Pattern.matches(ValidationConstant.EMAIL_REGEX, email);
    }

    private static boolean isValidPAN(String pan) {
        // PAN validation - PAN should be in the format ABCDE1234F
        return pan.matches(ValidationConstant.PAN_REGEX);
    }

    private static boolean isValidAadhar(String aadhar) {
        // Aadhar validation - Aadhar should be a 12-digit number
        return aadhar.matches(ValidationConstant.AADHAR_REGEX);
    }

     // Validation for GST
     private static boolean isValidGST(String gst) {
        // GST Number regex pattern for India
        // Use stream API to check if the value matches the regex pattern
        return gst.matches(ValidationConstant.GST_REGEX);
    }
    
    // Validation for MOBILENUMBER
    private static boolean isValidMobileNumber(String value) {
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
    private static boolean isValidDrivingLicense(String value) {
        // Regex pattern for driving license validation
        return value.matches(ValidationConstant.DL_REGEX);
    }
    
    // Validation for POSTALCODE
    private static boolean isValidPostalCode(String value) {
        // Postal code regex pattern for India (PIN code)    
        return value.matches(ValidationConstant.POSTAL_CODE_REGEX);
    }
    
    // Validation for POSTOFFICENAME
    private static boolean isValidPostOfficeName(String value) {
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

    public static String combineErrorMessages(Map<String, String> validationErrors) {
        StringBuilder errorMessage = new StringBuilder();

        for (Map.Entry<String, String> entry : validationErrors.entrySet()) {
            errorMessage.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return errorMessage.toString().trim();
    }
}

