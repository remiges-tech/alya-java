package com.remiges.alya.jobs;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.jobs.Initializer.InitBlock;

public class BatchProcessor {

    public BatchOutput DoBatchJob(InitBlock any, JsonNode jsonNode, int line, String input) {

        Map<String, String> data = new HashMap<>();
        data.put("transaction.txt", input);

        return BatchOutput.builder().messages("").result("Success").status(BatchStatus.BatchSuccess).blobRows(data)
                .error(ErrorCodes.NOERROR).build();
    }

    // public void MarkDone(BatchInitBlock any, String context, batch
    // BatchDetails_t)
}
