package com.remiges.alya.jobs;

import java.util.List;

import com.remiges.alya.entity.BatchRows;

import lombok.Data;

/**
 * Utility class for summarizing batch job results.
 */
@Data
public class SummaryUtils {

    // Counters for the number of failed, successful, and aborted batch rows
    Integer nfailed = 0;
    Integer nSuccess = 0;
    Integer naborted = 0;

    // Overall status of the batch
    BatchStatus SummaryStatus;

    /**
     * Calculates summary counters for the provided batch rows.
     *
     * @param batchrows the list of batch rows to summarize
     */
    public void calculateSummaryCounters(List<BatchRows> batchrows) {
        batchrows.forEach(row -> {
            // Increment counters based on the batch status of each row
            if (row.getBatchStatus().equals(BatchStatus.BatchAborted)) {
                naborted++;
            } else if (row.getBatchStatus().equals(BatchStatus.BatchFailed)) {
                nfailed++;
            } else if (row.getBatchStatus().equals(BatchStatus.BatchSuccess)) {
                nSuccess++;
            }
        });
    }

    /**
     * Determines the overall status of the batch based on the summary counters.
     */
    public void determineBatchStatus() {
        if (naborted > 0) {
            // If any row is aborted, the overall status is aborted
            SummaryStatus = BatchStatus.BatchAborted;
        } else if (nfailed > 0) {
            // If any row failed and none are aborted, the overall status is failed
            SummaryStatus = BatchStatus.BatchFailed;
        } else {
            // If all rows are successful, the overall status is successful
            SummaryStatus = BatchStatus.BatchSuccess;
        }
    }
}
