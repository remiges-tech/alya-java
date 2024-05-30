package com.remiges.alya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @EnableWebMvc
public class AliyaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AliyaApplication.class, args);
	}

}
/*This project will work as a library, 
1. In which I have implement logic for validating any DTO Request, with the help of custom annotation and Reg-ex.
2. Handle marshalling and unmarshalling JSON to Java objects and handle optional parameters.
3. Implement generation of response in the specified format. 
   This will involve using dictionaries for messages and error codes. */