package com.remiges.alya.jobs;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.service.BatchJobService;

@Component
public class Batch {

    BatchJobService batchjobSrv;

    Batch(BatchJobService batchjobSrv) {

        this.batchjobSrv = batchjobSrv;
    }

    public String batchSubmit(String app, String op, JsonNode jsonNode, List<BatchInput> batchInput, boolean waitabit) {

        // Set the batch status based on waitabit
        BatchStatus status = waitabit ? BatchStatus.BatchWait : BatchStatus.BatchQueued;

        UUID batchId = batchjobSrv.SaveBatch(app, op, jsonNode, batchInput, status);

        return batchId.toString();
    }

}
