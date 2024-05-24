// package com.remiges.alya.jsonutil.util;

// import com.fasterxml.jackson.annotation.JsonInclude;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;

// public final class JsonMapper {

//     private JsonMapper(){
//        // private constructor to prevent instantiation
//     }

//     private static final ObjectMapper objectMapper = new ObjectMapper();

    
//         /*we have to use this @JsonInclude(JsonInclude.Include.NON_NULL)
//          at entity or dto class level.
//          we instruct Jackson to exclude fields with null values from the JSON output during serialization.
//           During deserialization, if a field is not provided in the JSON, 
//           Jackson will set its value to null if the corresponding Java field is of a nullable type,
//            such as String*/

//     static {
//         // Configure ObjectMapper to exclude null values
//         objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//     }

//     // Marshalling / Sereialising Java Object to Json String 
//     public static String toJson(Object object) {
//         try {
//             return objectMapper.writeValueAsString(object);
//         } catch (JsonProcessingException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }

//     // UnMarshalling / Deserialising Json String to Java Object
//     public static <T> T fromJson(String json, Class<T> valueType) {
//         try {
//             return objectMapper.readValue(json, valueType);
//         } catch (JsonProcessingException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }

// }

// /*Private Constructor: By making the constructor private, you prevent instantiation of the JsonMapper class. This is a good practice for utility classes where you only need static methods.

// Static ObjectMapper: You have a static instance of ObjectMapper, which is thread-safe and efficient for reuse across multiple calls.

// toJson() Method: This method takes a Java object and converts it into a JSON string using ObjectMapper.writeValueAsString(). If any exception occurs during the process, it prints the stack trace and returns null.

// fromJson() Method: This method takes a JSON string and a target Java class type. It uses ObjectMapper.readValue() to convert the JSON string into a Java object of the specified class. If an exception occurs, it prints the stack trace and returns null.

// Generic Type for fromJson() Method: This method uses Java generics to allow flexibility in the return type, enabling the caller to specify the desired class type for unmarshalling. */