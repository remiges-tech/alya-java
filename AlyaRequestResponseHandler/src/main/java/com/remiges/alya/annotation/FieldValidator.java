package com.remiges.alya.annotation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
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
            case PHONE_NUMBER:
                return isValidPhoneNumber(value, value);    
            case MOBILE_NUMBER:
                return isValidMobileNumber(value, value);
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
            case VISA_CARD:
                return isValidVisaCard(value);
            case MASTER_CARD:
                return isValidMasterCard(value);
            case AMERICAN_EXPRESS:
                return isValidAmericanExpress(value);
            case DISCOVER_CARD:
                return isValidDiscoverCard(value);
            case JCB_CARD:
                return isValidJcbCard(value);
            case DINNER_CLUB_CARD:
                return isValidClubCard(value);  
            case ISBN:
                return isValidIsbn(value);  
            case IBAN:
                return isValidIban(value);  
            case DD:
                return isValidDd(value);         
            case VIN:
                return isValidVin(value);                      
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
        return email.matches(ValidationConstant.EMAIL_REGEX);
    }

    private boolean isValidPAN(String pan) {
        return pan.matches(ValidationConstant.PAN_REGEX);
    }

    private boolean isValidAadhar(String aadhar) {
        return aadhar.matches(ValidationConstant.AADHAR_REGEX);
    }

    private boolean isValidGST(String gst) {
        return gst.matches(ValidationConstant.GST_REGEX);
    }

    private boolean isValidMobileNumber(String mobileNumber, String region) {
        PhoneNumberUtil phoneNumber = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber number = phoneNumber.parse(mobileNumber, region);
            return phoneNumber.isValidNumber(number);
        } catch (NumberParseException e) {
            return false;
        }
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

    private boolean isValidVisaCard(String visaCard){
        return visaCard.matches(ValidationConstant.VISA_CARD);
    }

    private boolean isValidMasterCard(String masterCard){
        return masterCard.matches(ValidationConstant.MASTER_CARD);
    }

    private boolean isValidAmericanExpress(String americanExpress){
        return americanExpress.matches(ValidationConstant.AMERICAN_STRING_CARD);
    }

    private boolean isValidDiscoverCard(String discoverCard){
        return discoverCard.matches(ValidationConstant.DISCOVER_CARD);
    }
    private boolean isValidJcbCard(String jcbCard){
        return jcbCard.matches(ValidationConstant.JCB_CARD);
    }

    private boolean isValidClubCard(String clubCard){
        return clubCard.matches(ValidationConstant.DINNER_CLUB_CARD);
    }

    private boolean isValidIsbn(String isbn){
        return isbn.matches(ValidationConstant.ISBN);
    }

    private boolean isValidIban(String iban){
        return iban.matches(ValidationConstant.IBAN);
    }

    private boolean isValidDd(String dd){
        return dd.matches(ValidationConstant.DD);
    }

    public boolean isValidVin(String vin) {
        return vin.matches(ValidationConstant.VIN_PATTERN);
    }

    private boolean isValidPassport(String passport){
        return passport.matches(ValidationConstant.PASSPORT_NUMBER_PATTERN);
    }

     public boolean isValidPhoneNumber(String phoneNumber, String region) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber number = phoneNumberUtil.parse(phoneNumber, region);
            return phoneNumberUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            return false;
        }
    }


}

