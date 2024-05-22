package com.remiges.alya;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.remiges.alya.config.AppConfig;
import com.remiges.alya.jobs.BatchProcessor;
import com.remiges.alya.jobs.Initializer;
import com.remiges.alya.jobs.JobMgr;
import com.remiges.alya.jobs.JobMgrClient;
import com.remiges.alya.service.BatchJobService;

import redis.clients.jedis.Jedis;

@SpringBootApplication
@ComponentScan(basePackages = "com.remiges.alya")
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

		// JobMgrClient jobMgrcli = new JobMgrClient(); // jobMgr();

	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			JobMgrClient jobMgrcli = ctx.getBean(JobMgrClient.class);
			jobMgrcli.getJobmrg(); // This will call the method in MyComponent
			JobMgr jobMgr = jobMgrcli.getJobmrg();
			String KRA = "KRA";
		String PANENQUIRY = "PANENQURY";

			Initializer intr = new Initializer();
			// JobMgr jobm = new JobMgr(batchJobService);
			jobMgr.registerInitializer(KRA, intr);

			jobMgr.RegisterProcessor(KRA, PANENQUIRY, new BatchProcessor());

			jobMgr.DoJobs();

		};
	}

}
