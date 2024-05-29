package com.remiges.demoalya.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.jobs.Batch;
import com.remiges.alya.jobs.BatchInput;
import com.remiges.alya.jobs.BatchProcessor;
import com.remiges.alya.jobs.Initializer;
import com.remiges.alya.jobs.JobMgr;
import com.remiges.alya.jobs.JobMgrClient;
import com.remiges.demoalya.component.TransactionInitializer;
import com.remiges.demoalya.component.TransactionProcessor;

import io.hypersistence.utils.hibernate.type.json.internal.JacksonUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@EnableWebMvc
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private Batch batch;

    @Autowired
    private JobMgrClient jobMgrcli;

    @GetMapping("/home")
    public String getMethodName() {
        return "Welcme";
    }

    @PostMapping("/path")
    public String testSubmit(@RequestBody String entity) {
        // TODO: process POST request

        String KRA = "KRA";
        String PANENQUIRY = "PANENQURY";

        List<BatchInput> list = new ArrayList<>();
        int[] numbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        // Using enhanced for loop to iterate over the array
        for (int i : numbers) {
            System.out.println(i);

            BatchInput batchInput = BatchInput.builder().input("I" + i).line(i).build();
            list.add(batchInput);
        }

        batch.submitBatch("KRA", PANENQUIRY, JacksonUtil.toJsonNode("{" +
                "   \"title\": \"High-Performance Java Persistence\"," +
                "   \"author\": \"Vlad Mihalcea\"," + "   \"publisher\": \"Amazon\"," +
                "   \"price\": 44.99" + "}"), list, false);

        JobMgr jobMgr = jobMgrcli.getJobmrg();

        TransactionInitializer intr = new TransactionInitializer(); // JobMgr jobm = new

        jobMgr.registerInitializer(KRA, intr);

        jobMgr.RegisterProcessor(KRA, PANENQUIRY, new TransactionProcessor());

        jobMgr.DoJobs();

        return "entity";
    }

}
