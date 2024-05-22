package com.remiges.alya.jobs;

import java.util.List;

import com.remiges.alya.entity.BatchRows;

import lombok.Data;

@Data
public class SummaryUtils {
    Integer nfailed;
    Integer nSuccess;
    Integer naborted;

    BatchStatus SummaryStatus;

    public void calculateSummaryCounters(List<BatchRows> batchrows) {

        batchrows.forEach(row -> {
            if (row.getBatchStatus().equals(BatchStatus.BatchAborted)) {
                naborted++;
            } else if (row.getBatchStatus().equals(BatchStatus.BatchFailed)) {
                nfailed++;
            } else if (row.getBatchStatus().equals(BatchStatus.BatchSuccess)) {
                nSuccess++;
            }

        });

    }

    public void determineBatchStatus() {
        if (naborted > 0) {
            SummaryStatus = BatchStatus.BatchAborted;
        } else if (nfailed > 0) {
            SummaryStatus = BatchStatus.BatchFailed;
        } else {
            SummaryStatus = BatchStatus.BatchSuccess;
        }
    }

}
