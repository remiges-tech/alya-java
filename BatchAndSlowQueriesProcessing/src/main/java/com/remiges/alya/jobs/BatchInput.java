package com.remiges.alya.jobs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchInput {

    Integer line;
    String input;

}
