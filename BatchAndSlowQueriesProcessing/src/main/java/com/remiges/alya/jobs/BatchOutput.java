package com.remiges.alya.jobs;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BatchOutput {

    BatchStatus status;
    String result;
    String messages;
    Map<String, String> blobRows;
    ErrorCodes error;
}
