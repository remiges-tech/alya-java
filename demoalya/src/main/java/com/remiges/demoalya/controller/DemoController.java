package com.remiges.demoalya.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@EnableWebMvc
@RequestMapping("/demo")
public class DemoController {

    Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private Batch batch;

    @Autowired
    private JobMgrClient jobMgrcli;

    @GetMapping("/home")
    public String getMethodName() {
        return "Welcme";
    }

    @PostMapping("/submitbatch")
    public String submitBatch(@RequestParam("base64File") String base64File) {
        // TODO: process POST request

        String KRA = "KRA";
        String PANENQUIRY = "PANENQURY";

        List<Map<String, String>> inputdata = loadCSV(base64File);
        ObjectMapper objectMapper = new ObjectMapper();

        List<BatchInput> list = new ArrayList<>();
        AtomicInteger lineNumber = new AtomicInteger(1);
        inputdata.stream().forEach((map) -> {
            try {
                String jsonstr = objectMapper.writeValueAsString(map);

                BatchInput batchInput = BatchInput.builder().input(jsonstr).line(lineNumber.getAndIncrement()).build();
                list.add(batchInput);
            } catch (Exception ex) {
                logger.debug(ex.getMessage());
                System.out.println(ex.toString());
            }
        });

        batch.submit("KRA", PANENQUIRY, JacksonUtil.toJsonNode("{" +
                "   \"title\": \"High-Performance Java Persistence\"," +
                "   \"author\": \"Vlad Mihalcea\"," + "   \"publisher\": \"Amazon\"," +
                "   \"price\": 44.99" + "}"), list, false);

        JobMgr jobMgr = jobMgrcli.getJobmrg();

        TransactionInitializer intr = new TransactionInitializer(); // JobMgr jobm = new
        try {
            jobMgr.registerInitializer(KRA, intr);

            jobMgr.RegisterProcessor(KRA, PANENQUIRY, new TransactionProcessor());
        } catch (Exception ex) {

        }
        jobMgr.DoJobs();

        return "entity";
    }

    public static String convertEntryToJson(Map.Entry<String, ?> entry) {
        // Create a map containing the key-value pair from the entry
        Map<String, Object> map = new HashMap<>();
        map.put(entry.getKey(), entry.getValue());

        // Use Jackson's ObjectMapper to serialize the map to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Map<String, String>> loadCSV(String base64File) {
        logger.info("Received request to convert Base64-encoded CSV to JSON.");
        List<Map<String, String>> listOfDetails = new ArrayList<>();
        try {
            // Decode the Base64-encoded file
            byte[] decodedBytes = Base64.getDecoder().decode(base64File);
            logger.debug("Base64 file decoded successfully.");

            // Create a reader for the decoded bytes
            InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(decodedBytes));

            // Parse the CSV file with the first record as the header
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
            CSVParser csvParser = new CSVParser(reader, csvFormat);

            // Convert the CSV records to a list of maps using streams
            AtomicInteger lineNumber = new AtomicInteger(1);
            listOfDetails = csvParser.getRecords().stream()
                    .map(record -> {
                        Map<String, String> recordMap = new LinkedHashMap<>(record.toMap());
                        // recordMap.put("lineNumber", String.valueOf(lineNumber.getAndIncrement()));
                        return recordMap;
                    })
                    .collect(Collectors.toList());
            logger.debug("CSV parsed successfully and convert it in list of json !.");
            // Close the parser and reader
            csvParser.close();
            reader.close();
            return listOfDetails;

        } catch (Exception ex) {
            logger.debug("exception !.");
            return null;
        }
    }
}
