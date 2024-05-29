package com.remiges.demoalya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "com.remiges.alya", "com.remiges.demoalya" })
@EnableJpaRepositories(basePackages = { "com.remiges.alya.repository" })
@EntityScan(basePackages = { "com.remiges.alya.entity" })
public class DemoalyaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoalyaApplication.class, args);
	}

}
