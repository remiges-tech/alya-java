package com.alya.alyawscservice.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.alya.alyawscservice.constant.ValidationConstant;
import com.alya.alyawscservice.json.ErrorMessage;
import com.alya.alyawscservice.model.RequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class AlyaValidation {

      /*
     * Validates the provided request data.
     *
     * This method performs validation on the fields of the {@link RequestDTO} object. It checks if
     * each field meets the required criteria and returns a map of validation errors.
     *
     * @param request the request data to validate
     * @return a map where the keys are field names and the values are error messages
     */
//     public static Map<String, String> alyaValidator(RequestDTO request) {
//         Map<String, String> errors = new HashMap<>();

//         // Validate name
//         if (!isValidName(request.getName())) {
//             errors.put("name", "Name must contain only letters and spaces");
//         }

//         // Validate email
//         if (!isValidEmail(request.getEmail())) {
//             errors.put("email", "Email is not valid");
//         }

//         // Validate PAN
//         if (!isValidPAN(request.getPan())) {
//             errors.put("pan", "PAN must be in the format ABCDE1234F");
//         }

//         // Validate Aadhar
//         if (!isValidAadhar(request.getAadhar())) {
//             errors.put("aadhar", "Aadhar must be a 12-digit number");
//         }

//         //Validate GST
//         if(!isValidGST(request.getGst())){
//             errors.put("gst", "GST formate is incorrect");
//         }

//         //Validate Mobile Number
//         if(!isValidMobileNumber(request.getMobileNumber())){
//             errors.put("Mobile Number", "Mobile number is incorrect");
//         }

//         //Validate Driving License
//         if(!isValidDrivingLicense(request.getDrivingLicense())){
//             errors.put("DL", "DL number is not proper");
//         }

//         //Validate POSTALCODE
//         if(!isValidPostalCode(request.getPostalCode())){
//             errors.put("Postal Code", "Postal Code is not in correct formate");
//         }

//         //Validate POSTOFFICENAME
//         if(!isValidPostOfficeName(request.getPostOfficeName())){
//             errors.put("Post Office", "Post Office Name formate is not proper");
//         }

//         //Validate Phone Number
//         if(!isValidPhoneNumber(request.getPhoneNumber())){
//             errors.put("Phone Number", "Phone Number formate is not correct");
//         }

//         return errors;
//     }

//     private static boolean isValidName(String name) {
//         // Name should contain only letters and spaces
//         return name.matches(ValidationConstant.NAME_REGEX);
//     }

//     private static boolean isValidEmail(String email) {
//         // Basic email pattern validation as per regex
//         return Pattern.matches(ValidationConstant.EMAIL_REGEX, email);
//     }

//     private static boolean isValidPAN(String pan) {
//         // PAN validation - PAN should be in the format ABCDE1234F
//         return pan.matches(ValidationConstant.PAN_REGEX);
//     }

//     private static boolean isValidAadhar(String aadhar) {
//         // Aadhar validation - Aadhar should be a 12-digit number
//         return aadhar.matches(ValidationConstant.AADHAR_REGEX);
//     }

//      // Validation for GST
//      private static boolean isValidGST(String gst) {
//         // GST Number regex pattern for India
//         return gst.matches(ValidationConstant.GST_REGEX);
//     }
    
//     // Validation for MOBILENUMBER
//     private static boolean isValidMobileNumber(String mobileNumber) {
//         // Implement Mobile Number validation logic
//         // PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
//         //     try {
//         //         Phonenumber.PhoneNumber number = phoneNumberUtil.parse(mobileNumber, null); // No country code specified
//         //         return phoneNumberUtil.isValidNumber(number);
//         //     } catch (NumberParseException e) {
//         //         System.out.println("NumberParseException was thrown: " + e.toString());
//         //         return false;
//         // }
//         return mobileNumber.matches(ValidationConstant.MOBILE_NUMBER_REGEX);
//     }

//     // Validation for DRIVINGLICENSE
//     private static boolean isValidDrivingLicense(String drivingLicense) {
//         // Regex pattern for driving license validation
//         return drivingLicense.matches(ValidationConstant.DRIVING_LICENSE_REGEX);
//     }
    
//     // Validation for POSTALCODE
//     private static boolean isValidPostalCode(String postalCode) {
//         // Postal code regex pattern for India (PIN code)    
//         return postalCode.matches(ValidationConstant.POSTAL_CODE_REGEX);
//     }
    
//     // Validation for POSTOFFICENAME
//     private static boolean isValidPostOfficeName(String postOfficeName) {
//         // Post Office Name should not be null and should contain only letters, spaces, and hyphens
//         return postOfficeName != null && postOfficeName.matches(ValidationConstant.POST_OFFICE_NAME_REGEX);
//     }
    

//     public static boolean isValidPhoneNumber(String phoneNumber) {
//         // PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
//         // try {
//         //     Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, null); // No country code specified
//         //     return phoneNumberUtil.isValidNumber(number);
//         // } catch (NumberParseException e) {
//         //     System.out.println("NumberParseException was thrown: " + e.toString());
//         //     return false;
//         // }
//         return phoneNumber.matches(ValidationConstant.PHONE_NUMBER_REGEX);
//     }
//    /*
//  * Combines the validation errors into a structured error message.
//  *
//  * @param validationErrors A HashMap containing validation errors, where the key is the field name and the value is the error message.
//  * @return A JSON string representing the combined error messages, ready to be used as an error response.
//  */
// public static String combineErrorMessages(Map<String, String> validationErrors) {
//     // Create a list to store error messages
//     List<ErrorMessage> errorMessages = new ArrayList<>();

//     // Populate the errorMessages list with the provided validation errors
//     for (Map.Entry<String, String> entry : validationErrors.entrySet()) {
//         errorMessages.add(new ErrorMessage(null, 0, entry.getKey(), entry.getValue()));
//     }

//     // Create an ObjectMapper instance for JSON serialization
//     ObjectMapper objectMapper = new ObjectMapper();

//     // Create an ObjectNode to represent the JSON structure
//     ObjectNode result = objectMapper.createObjectNode();

//     // Set the error status in the JSON structure
//     result.put("status", "error");

//     // Create an empty object for the 'data' field in the JSON structure
//     result.set("data", objectMapper.createObjectNode());

//     // Convert the errorMessages list to a JSON tree and set it as the 'messages' field in the JSON structure
//     result.set("messages", objectMapper.valueToTree(errorMessages));

//     try {
//         // Serialize the JSON structure to a JSON string
//         return objectMapper.writeValueAsString(result);
//     } catch (JsonProcessingException e) {
//         // If an error occurs during serialization, print the stack trace and return null
//         e.printStackTrace();
//         return null;
//     }
// }

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
    validateField(request.getMobileNumber(), "Mobile Number", "Mobile number is incorrect", ValidationConstant.MOBILE_NUMBER_REGEX, errors);
    validateField(request.getDrivingLicense(), "DL", "DL number is not proper", ValidationConstant.DRIVING_LICENSE_REGEX, errors);
    validateField(request.getPostalCode(), "Postal Code", "Postal Code is not in correct format", ValidationConstant.POSTAL_CODE_REGEX, errors);
    validateField(request.getPostOfficeName(), "Post Office", "Post Office Name format is not proper", ValidationConstant.POST_OFFICE_NAME_REGEX, errors);
    validateField(request.getPhoneNumber(), "Phone Number", "Phone Number format is not correct", ValidationConstant.PHONE_NUMBER_REGEX, errors);

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
        errors.put(fieldName, errorMessage);
    }
}

}
