package com.remiges.alya.jobs;

import java.util.UUID;

public class SlowQuery {
	
    public String submit(String app, String op, String context, String input) {
        String reqID = UUID.randomUUID().toString(); // Generate unique request ID
        
        // Insert record into batches table
        // Insert record into batchrows table

        return reqID;
    }
}
