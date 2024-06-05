package com.remiges.alya.jobs;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
public class BatchOutput {

    BatchStatus status;
    String result;
    String messages;
    Map<String, String> blobRows;

    ErrorCodes error;
}
