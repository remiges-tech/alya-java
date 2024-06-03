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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remiges.alya.jobs.AlyaBatchResponse;
import com.remiges.alya.jobs.Batch;
import com.remiges.alya.jobs.BatchInput;
import com.remiges.alya.jobs.BatchOutputResult;
import com.remiges.alya.jobs.BatchProcessor;
import com.remiges.alya.jobs.BatchResultDTO;
import com.remiges.alya.jobs.BatchStatus;
import com.remiges.alya.jobs.Initializer;
import com.remiges.alya.jobs.JobMgr;
import com.remiges.alya.jobs.JobMgrClient;
import com.remiges.demoalya.component.TransactionInitializer;
import com.remiges.demoalya.component.TransactionProcessor;
import com.remiges.demoalya.dto.BatchAppendDto;
import com.remiges.demoalya.dto.BatchListDto;

import ch.qos.logback.core.util.Duration;
import io.hypersistence.utils.hibernate.type.json.internal.JacksonUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@EnableWebMvc
@RequestMapping("/Batch")
public class BatchController {

    Logger logger = LoggerFactory.getLogger(BatchController.class);

    @Autowired
    private Batch batch;

    @Autowired
    ObjectMapper objectMapper;

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

        String batchId = batch.submit("KRA", PANENQUIRY, JacksonUtil.toJsonNode("{" +
                "   \"fileName\": \"Transaction.csv\"}"), list, false);

        JobMgr jobMgr = jobMgrcli.getJobmrg();

        logger.info(jobMgr.toString());

        /*
         * JobMgr jobMgr2 = jobMgrcli.getJobMgr();
         * 
         * logger.info(jobMgr2.toString());
         */
        TransactionInitializer intr = new TransactionInitializer(); // JobMgr jobm = new
        try {
            jobMgr.registerInitializer(KRA, intr);

            jobMgr.RegisterProcessor(KRA, PANENQUIRY, new TransactionProcessor());

            /*
             * jobMgr2.registerInitializer(KRA, intr);
             * 
             * jobMgr2.RegisterProcessor(KRA, PANENQUIRY, new TransactionProcessor());
             */
        } catch (Exception ex) {
            logger.debug("Exception while registering processor Manager{}", ex);
        }
        try {
            jobMgr.DoJobs();
            // jobMgr2.DoJobs();
        } catch (Exception ex) {
            logger.debug("Exception while starting Job Manager{}", ex);
        }

        return batchId;
    }

    @PostMapping("/abort")
    public String abortBatch(@RequestParam String batchId) {
        // TODO: process POST request
        String strerror = "";
        try {

            batch.abort(batchId);
            strerror = "Batch aborted successfully";
        } catch (Exception ex) {
            strerror = " Exception while aborting batch" + ex.getMessage();

        }

        return strerror;
    }

    @GetMapping("list")
    public ResponseEntity<?> listBatches(@RequestBody BatchListDto lstbatchDto) {

        List<BatchResultDTO> batchList = batch.list(lstbatchDto.getApp(), lstbatchDto.getOp(), lstbatchDto.getAge());
        String error = "No batch found";
        if (batchList.size() <= 0) {
            return ResponseEntity.ok().body(error);
        }

        return ResponseEntity.ok().body(batchList);

    }

    @PostMapping("BatchAppend")
    public ResponseEntity<String> BatchAppend(@RequestBody BatchAppendDto dBatchAppendDto) {
        // TODO: process POST request
        try {
            List<Map<String, String>> inputdata = loadCSV(dBatchAppendDto.getBase64File());

            List<BatchInput> list = new ArrayList<>();
            AtomicInteger lineNumber = new AtomicInteger(1);
            inputdata.stream().forEach((map) -> {
                try {
                    String jsonstr = objectMapper.writeValueAsString(map);

                    BatchInput batchInput = BatchInput.builder().input(jsonstr).line(lineNumber.getAndIncrement())
                            .build();
                    list.add(batchInput);
                } catch (Exception ex) {
                    logger.debug(ex.getMessage());
                    System.out.println(ex.toString());
                }
            });

            AlyaBatchResponse res = batch.append(dBatchAppendDto.getBatchId(), list, dBatchAppendDto.getWaitoff());

            return ResponseEntity.ok(res.toString());

        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }

    }

    @PostMapping("batchWaitOff")
    public ResponseEntity<String> batchWaitOff(@RequestParam String batchId) {
        try {

            AlyaBatchResponse res = batch.waitOff(batchId);
            return ResponseEntity.ok(res.toString());
        } catch (Exception e) {
            return ResponseEntity.ok(e.toString());
        }

    }

    @GetMapping("getBatchStatus")
    public ResponseEntity<BatchOutputResult> getBatchStatus(@RequestParam String batchId) {
        BatchOutputResult output;
        output = batch.done(batchId);

        if (output.getStatus().equals(BatchStatus.BatchInProgress)
                || output.getStatus().equals(BatchStatus.BatchQueued)) {
        } else {
            output.getNsuccess();
            output.getNaborted();
            output.getNfailed();
            output.getOutputFiles();
        }

        return ResponseEntity.ok(output);
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
