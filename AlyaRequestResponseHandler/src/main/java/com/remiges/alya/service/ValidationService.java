// package com.remiges.alya.service;

// import org.springframework.stereotype.Service;

// import com.remiges.alya.model.RequestDTO;

// import javax.validation.ConstraintViolation;
// import javax.validation.Validation;
// import javax.validation.Validator;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Set;

// @Service
// public class ValidationService {

//     private final Validator validator;

//     public ValidationService() {
//         this.validator = Validation.buildDefaultValidatorFactory().getValidator();
//     }

//     public List<String> validateDTO(RequestDTO requestDTO ){
//         List<String> errors = new ArrayList<>();

//         Set<ConstraintViolation<RequestDTO>> violations = validator.validate(requestDTO);

//         for (ConstraintViolation<RequestDTO> violation : violations) {
//             errors.add(violation.getMessage());
//         }

//         return errors;
//     }
// }
