package com.remiges.demoalya.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.jobs.BatchInitBlocks;
import com.remiges.alya.jobs.BatchOutput;
import com.remiges.alya.jobs.BatchProcessor;

public class TransactionProcessor implements BatchProcessor {

    @Override
    public BatchOutput DoBatchJob(BatchInitBlocks any, JsonNode context, int line, JsonNode input) {
        // TODO Auto-generated method stub

        throw new UnsupportedOperationException("Unimplemented method 'DoBatchJob'");
    }

}
