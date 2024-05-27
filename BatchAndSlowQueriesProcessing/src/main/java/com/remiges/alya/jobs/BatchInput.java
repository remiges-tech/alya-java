package com.remiges.alya.jobs;

import com.remiges.alya.entity.Batches;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchInput {

    Integer line;
    String input;

}
