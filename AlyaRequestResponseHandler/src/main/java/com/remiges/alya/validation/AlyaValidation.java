package com.remiges.alya.validation;

import java.util.HashMap;
import java.util.Map;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.remiges.alya.constant.ValidationConstant;
import com.remiges.alya.model.RequestDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlyaValidation {

    /**
     * Validates the fields in the provided request DTO.
     *
     * @param request the request DTO containing fields to validate
     * @return a map where the keys are field names and the values are error messages
     */
    public static Map<String, String> alyaValidator(RequestDTO request) {
        Map<String, String> errors = new HashMap<>();

        validateField(request.getName(), "name", "Name must contain only letters and spaces", ValidationConstant.NAME_REGEX, errors);
        validateField(request.getEmail(), "email", "Email is not valid", ValidationConstant.EMAIL_REGEX, errors);
        validateField(request.getPan(), "pan", "PAN must be in the format ABCDE1234F", ValidationConstant.PAN_REGEX, errors);
        validateField(request.getAadhar(), "aadhar", "Aadhar must be a 12-digit number", ValidationConstant.AADHAR_REGEX, errors);
        validateField(request.getGst(), "gst", "GST format is incorrect", ValidationConstant.GST_REGEX, errors);
        validateMobileNumber(request.getMobileNumber(), errors);
        validateField(request.getDrivingLicense(), "DL", "DL number is not proper", ValidationConstant.DRIVING_LICENSE_REGEX, errors);
        validateField(request.getPostalCode(), "Postal Code", "Postal Code is not in correct format", ValidationConstant.POSTAL_CODE_REGEX, errors);
        validateField(request.getPostOfficeName(), "Post Office", "Post Office Name format is not proper", ValidationConstant.POST_OFFICE_NAME_REGEX, errors);
        validatePhoneNumber(request.getPhoneNumber(), errors);

        return errors;
    }

    /**
     * Validates a field value against a regular expression pattern.
     *
     * @param value       the value of the field to validate
     * @param fieldName   the name of the field being validated
     * @param errorMessage the error message to associate with the field if validation fails
     * @param regex       the regular expression pattern for validation
     * @param errors      the map to store validation errors
     */
    private static void validateField(String value, String fieldName, String errorMessage, String regex, Map<String, String> errors) {
        if (value == null || !value.matches(regex)) {
            log.info("Validating the request DTO using regex.");
            errors.put(fieldName, errorMessage);
        }
    }

    /**
     * Validates a mobile number using Google's phone number library.
     *
     * @param mobileNumber the mobile number to validate
     * @param errors       the map to store validation errors
     */
    private static void validateMobileNumber(String mobileNumber, Map<String, String> errors) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber number = phoneNumberUtil.parse(mobileNumber, null); // No country code specified
            if (!phoneNumberUtil.isValidNumber(number)) {
                errors.put("Mobile Number", "Mobile number is incorrect");
            }
        } catch (NumberParseException e) {
            errors.put("Mobile Number", "Invalid mobile number format");
        }
    }

    /**
     * Validates a phone number using Google's phone number library.
     *
     * @param phoneNumber the phone number to validate
     * @param errors      the map to store validation errors
     */
    private static void validatePhoneNumber(String phoneNumber, Map<String, String> errors) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber number = phoneNumberUtil.parse(phoneNumber, null); // No country code specified
            if (!phoneNumberUtil.isValidNumber(number)) {
                errors.put("Phone Number", "Phone Number format is not correct");
            }
        } catch (NumberParseException e) {
            errors.put("Phone Number", "Invalid phone number format");
        }
    }
}
