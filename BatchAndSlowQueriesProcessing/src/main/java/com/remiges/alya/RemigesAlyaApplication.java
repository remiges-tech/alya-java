package com.remiges.alya;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import redis.clients.jedis.Jedis;

@SpringBootApplication
public class RemigesAlyaApplication {
	@Bean
	public static Jedis jedis() {
		return new Jedis("localhost", 6379);
	}

	@Bean
	public static Logger logger() {
		return Logger.getLogger("BatchProcessorLogs");
	}

	public static void main(String[] args) {
		SpringApplication.run(RemigesAlyaApplication.class, args);	


	}

}
