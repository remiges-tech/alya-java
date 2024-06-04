package com.remiges.alya.annotation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.remiges.alya.constant.ValidationConstant;

/**
 * Validator implementation for the {@link ValidField} annotation.
 * This validator checks if the value of a field annotated with {@link ValidField}
 * conforms to the specified validation rules based on the field type.
 */

public class FieldValidator implements ConstraintValidator<ValidField, String> {

    private FieldType type;

      /**
     * Initializes the validator with the specified field type.
     *
     * @param constraintAnnotation The annotation instance containing the field type.
     */
    @Override
    public void initialize(ValidField constraintAnnotation) {
        this.type = constraintAnnotation.fieldType();
    }

      /**
     * Validates the given field value based on its type.
     *
     * @param value   The field value to validate.
     * @param context The validation context.
     * @return True if the value is valid according to the field type; otherwise, false.
     */

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        switch (type) {
            case NAME:
                return isValidName(value);
            case EMAIL:
                return isValidEmail(value);
            case PAN:
                return isValidPAN(value);
            case AADHAR:
                return isValidAadhar(value);
            // Add cases for more field types
            case MOBILE_NUMBER:
                return isValidMobileNumber(value);
            case GST:
                return isValidGST(value);  
            case DRIVING_LICENSE:
                return isValidDL(value);   
            case POSTAL_CODE:
                return isValidPostalCode(value);
            case POST_OFFICE_NAME:
                return isValidPostOffice(value);  
            case PASSWORD:
                return isValidPassword(value);
            case URL:
                return isValidUrl(value); 
            case CARD_NUMBER:
                return isValidCardNumber(value);     
            case PASSPORT_NUMBER:
                return isValidPassport(value);
            default:
                return false;
        }
    }

     /**
     * Validates a name field.
     *
     * @param value The name to validate.
     * @return True if the name is valid; otherwise, false.
     */

    private boolean isValidName(String name){
        return name.matches(ValidationConstant.NAME_REGEX);
    }

    private boolean isValidEmail(String email) {
        // Add your email regex pattern here
        return email.matches(ValidationConstant.EMAIL_REGEX);
    }

    private boolean isValidPAN(String pan) {
        // Add your PAN regex pattern here
        return pan.matches(ValidationConstant.PAN_REGEX);
    }

    private boolean isValidAadhar(String aadhar) {
        // Add your Aadhar regex pattern here
        return aadhar.matches(ValidationConstant.AADHAR_REGEX);
    }

    private boolean isValidGST(String gst) {
        // Add your GST validation logic here
        return gst.matches(ValidationConstant.GST_REGEX);
    }

    private boolean isValidMobileNumber(String mobileNumber) {
        // Add your mobile number validation logic here
        return true;
    }

    private boolean isValidDL(String drivingLicense){
        return drivingLicense.matches(ValidationConstant.DRIVING_LICENSE_REGEX);
    }

    private boolean isValidPostalCode(String postalCode){
        return postalCode.matches(ValidationConstant.POSTAL_CODE_REGEX);
    }

    private boolean isValidPostOffice(String postOfficeName){
        return postOfficeName.matches(ValidationConstant.POST_OFFICE_NAME_REGEX);
    }

    private boolean isValidPassword(String password){
        return password.matches(ValidationConstant.PASSWORD_PATTERN);
    }

    private boolean isValidUrl(String url){
        return url.matches(ValidationConstant.URL_PATTERN);
    }

    private boolean isValidCardNumber(String cardNumber){
        return cardNumber.matches(ValidationConstant.CARD_NUMBER_PATTERN);
    }

    private boolean isValidPassport(String passport){
        return passport.matches(ValidationConstant.PASSPORT_NUMBER_PATTERN);
    }


}

