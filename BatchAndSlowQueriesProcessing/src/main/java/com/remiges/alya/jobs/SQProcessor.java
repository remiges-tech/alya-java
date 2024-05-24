package com.remiges.alya.jobs;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.jobs.Initializer.InitBlock;

public class SQProcessor {

    public BatchOutput DoSlowQuery(InitBlock any, JsonNode jsonNode, String input) {

        return BatchOutput.builder().build();
    }

    // public void MarkDone(BatchInitBlock any, String context, batch
    // BatchDetails_t)

}
