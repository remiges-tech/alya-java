package com.remiges.alya.jobs;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchOutput {

    BatchStatus status;
    String result;
    String messages;
    Map<String, String> blobRows;
    ErrorCodes error;
}
