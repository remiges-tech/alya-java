package com.remiges.alya.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.remiges.alya.constant.ValidationConstant;
import com.remiges.alya.dto.RequestParameterDTO;
import com.remiges.alya.exception.CustomException;

@Service
public class ValidationService {

    public String validateUser(RequestParameterDTO userDTO) {
        if (userDTO.getPan() != null && !userDTO.getPan().isEmpty()) {
            return validatePan(userDTO.getPan());
        } else if (userDTO.getAadhar() != null && !userDTO.getAadhar().isEmpty()) {
            return validateAadhaar(userDTO.getAadhar());
        } else if (userDTO.getGst() != null && !userDTO.getGst().isEmpty()) {
            return validateGST(userDTO.getGst());
        } else if (userDTO.getDrivingLicense() != null && !userDTO.getDrivingLicense().isEmpty()) {
            return validateDrivingLicense(userDTO.getDrivingLicense());
        } else if (userDTO.getPostalCode() != null && !userDTO.getPostalCode().isEmpty()) {
            return validatePostalCode(userDTO.getPostalCode());
        } else if (userDTO.getPostOfficeName() != null && !userDTO.getPostOfficeName().isEmpty()) {
            return validatePostOfficeName(userDTO.getPostOfficeName());
        } else if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().isEmpty()) {
            return validatePhoneNumber(userDTO.getPhoneNumber());
        }
        throw new CustomException("No field provided for validation.");
    }

   /*  public String validateUser(RequestParameterDTO userDTO) {
        Map<String, Supplier<String>> validators = new LinkedHashMap<>();
        
        validators.put(userDTO.getPan(), () -> validatePan(userDTO.getPan()));
        validators.put(userDTO.getAadhar(), () -> validateAadhaar(userDTO.getAadhar()));
        validators.put(userDTO.getGst(), () -> validateGST(userDTO.getGst()));
        validators.put(userDTO.getDrivingLicense(), () -> validateDrivingLicense(userDTO.getDrivingLicense()));
        validators.put(userDTO.getPostalCode(), () -> validatePostalCode(userDTO.getPostalCode()));
        validators.put(userDTO.getPostOfficeName(), () -> validatePostOfficeName(userDTO.getPostOfficeName()));
        validators.put(userDTO.getPhoneNumber(), () -> validatePhoneNumber(userDTO.getPhoneNumber()));
        
        for (Map.Entry<String, Supplier<String>> entry : validators.entrySet()) {
            if (entry.getKey() != null && !entry.getKey().isEmpty()) {
                return entry.getValue().get();
            }
        }
        
        throw new CustomException("No field provided for validation.");
    }
     This part of code is the optimization of above code*/
    

    private static String validatePan(String pan) {
        return Pattern.compile(ValidationConstant.PAN_REGEX).matcher(pan).matches() ? "Entered PAN value is proper" : "Improper PAN Value Entered";
    }

    private static String validateAadhaar(String aadhaar) {
        return Pattern.compile(ValidationConstant.AADHAR_REGEX).matcher(aadhaar).matches() ? "Entered Aadhaar value is proper" : "Improper Aadhaar Value Entered";
    }

    private static String validateGST(String gst) {
        return Pattern.compile(ValidationConstant.GST_REGEX).matcher(gst).matches() ? "Entered GST value is proper" : "Improper GST Value Entered";
    }

    private static String validateDrivingLicense(String license) {
        return Pattern.compile(ValidationConstant.DL_REGEX).matcher(license).matches() ? "Entered Driving License value is proper" : "Improper Driving License Value Entered";
    }

    private static String validatePostalCode(String postalCode) {
        return Pattern.compile(ValidationConstant.POSTAL_CODE_REGEX).matcher(postalCode).matches() ? "Entered Postal Code value is proper" : "Improper Postal Code Value Entered";
    }

    private static String validatePostOfficeName(String postOfficeName) {
        return Pattern.compile(ValidationConstant.POST_OFFICE_NAME).matcher(postOfficeName).matches() ? "Entered Post Office Name value is proper" : "Improper Post Office Name Value Entered";
    }

    private static String validatePhoneNumber(String phoneNumber) {
        return IsVaildPhoneNumber(phoneNumber) ? "Entered Phone Number value is proper" : "Improper Phone Number Value Entered";
    }
    
    public static boolean IsVaildPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber number = phoneNumberUtil.parse(phoneNumber, null); // No country code specified
            return phoneNumberUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            System.out.println("NumberParseException was thrown: " + e.toString());
            return false;
        }
    }

 
}


