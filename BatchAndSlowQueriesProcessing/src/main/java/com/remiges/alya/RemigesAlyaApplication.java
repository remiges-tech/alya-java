package com.remiges.alya;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import com.remiges.alya.jobs.Batch;
import com.remiges.alya.jobs.BatchInput;
import com.remiges.alya.jobs.BatchProcessor;
import com.remiges.alya.jobs.Initializer;
import com.remiges.alya.jobs.JobMgr;
import com.remiges.alya.jobs.JobMgrClient;
import com.remiges.alya.service.BatchJobService;

import io.hypersistence.utils.hibernate.type.json.internal.JacksonUtil;
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
			String KRA = "KRA";
			String PANENQUIRY = "PANENQURY";

			JobMgrClient jobMgrcli = ctx.getBean(JobMgrClient.class);
			jobMgrcli.getJobmrg(); // This will call the method in MyComponent

			Batch batch = ctx.getBean(Batch.class);

			List<BatchInput> list = new ArrayList<>();
			BatchInput batchInput = BatchInput.builder().input("input1").line(1).build();
			BatchInput batchInput2 = BatchInput.builder().input("input2").line(2).build();

			list.add(batchInput);
			list.add(batchInput2);

			batch.batchSubmit("KRA", PANENQUIRY, JacksonUtil.toJsonNode("{" +
					"   \"title\": \"High-Performance Java Persistence\"," +
					"   \"author\": \"Vlad Mihalcea\"," +
					"   \"publisher\": \"Amazon\"," +
					"   \"price\": 44.99" +
					"}"), list, false);

			JobMgr jobMgr = jobMgrcli.getJobmrg();

			Initializer intr = new Initializer();
			// JobMgr jobm = new JobMgr(batchJobService);
			jobMgr.registerInitializer(KRA, intr);

			jobMgr.RegisterProcessor(KRA, PANENQUIRY, new BatchProcessor());

			jobMgr.DoJobs();

		};
	}

}
