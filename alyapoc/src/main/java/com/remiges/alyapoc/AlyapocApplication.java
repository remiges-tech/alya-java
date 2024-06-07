package com.remiges.alyapoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = { "com.remiges.alya", "com.remiges.alyapoc" })
public class AlyapocApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlyapocApplication.class, args);
	}

}
