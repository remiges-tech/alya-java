package com.remiges.alya.jobs;

import java.util.List;
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
    Map<String, List<Map<String, String>>> slowQueryblobRows;

    ErrorCodes error;
}
