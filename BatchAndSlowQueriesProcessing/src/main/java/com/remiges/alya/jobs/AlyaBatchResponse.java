package com.remiges.alya.jobs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class AlyaBatchResponse {

    private String batchID;
    private int totalRows;




}

