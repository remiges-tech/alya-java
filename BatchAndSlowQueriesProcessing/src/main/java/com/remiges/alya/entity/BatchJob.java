package com.remiges.alya.entity;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;
import lombok.ToString;

@Data
public class BatchJob {

    // java.lang.String,java.lang.String,java.util.UUID,java.lang.Long,com.fasterxml.jackson.databind.JsonNode,java.lang.Integer,java.lang.String],
    BatchJob(String app, String op, UUID Id, Long rowId, JsonNode context, Integer line, String input) {
        this.app = app;
        this.op = op;
        this.id = Id;
        this.rowId = rowId;
        this.context = context;
        this.line = line;
        this.input = input;
    }

    String app;

    String op;

    UUID id;

    Long rowId; // unique row ID from batchrows table

    JsonNode context;

    Integer line;

    String input;

}
