package com.remiges.alya.jobs;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public interface BatchProcessor {

    public BatchOutput DoBatchJob(BatchInitBlocks any, JsonNode jsonNode, int line, String input);

    // public void MarkDone(BatchInitBlock any, String context, batch
    // BatchDetails_t)
}
