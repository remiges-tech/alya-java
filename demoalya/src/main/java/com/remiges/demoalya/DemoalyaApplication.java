package com.remiges.demoalya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.jobs.BatchOutput;
import com.remiges.demoalya.component.SlowQueryInitBlock;
import com.remiges.demoalya.component.SlowQueryProcessor;

@SpringBootApplication(scanBasePackages = { "com.remiges.alya", "com.remiges.demoalya" })
@EnableJpaRepositories(basePackages = { "com.remiges.alya.repository" })
@EntityScan(basePackages = { "com.remiges.alya.entity" })
public class DemoalyaApplication {

	public static void main(String[] args) {
        SlowQueryInitBlock initBlock = new SlowQueryInitBlock();
        JsonNode context = null; // You can initialize this if needed
        String inputQuery = "SELECT * FROM batches"; // Replace with your SQL query

        // Instantiate the SlowQueryProcessor
        SlowQueryProcessor processor = new SlowQueryProcessor();

        // Execute the method
        BatchOutput output = processor.DoSlowQuery(initBlock, context, inputQuery);

        // Print or process the output as needed
        System.out.println("Batch status: " + output.getStatus());
       System.out.println("Error code: " + output.getBlobRows());
        // Process blobMap if needed
       
		SpringApplication.run(DemoalyaApplication.class, args);


	}

}
