package com.remiges.alya.jobs;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class SQProcessor {

    public abstract BatchOutput DoSlowQuery(BatchInitBlocks any, JsonNode jsonNode, String input);
    // public void MarkDone(BatchInitBlock any, String context, batch
    // BatchDetails_t)

}
