package com.remiges.alya.jobs;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.entity.Batches;
import com.remiges.alya.service.BatchJobService;

import jakarta.transaction.Transactional;

/**
 * Component for managing batch operations.
 */
@Component
public class Batch {

    private final BatchJobService batchJobService;

    /**
     * Constructs a new Batch component.
     *
     * @param batchJobService the BatchJobService instance
     */
    public Batch(BatchJobService batchJobService) {
        this.batchJobService = batchJobService;
    }

    /**
     * Submits a batch job.
     *
     * @param app         the application name
     * @param op          the operation name
     * @param jsonNode    the JSON context
     * @param batchInput  the list of batch inputs
     * @param waitabit    whether to wait before processing
     * @return the ID of the submitted batch
     */
    public String submitBatch(String app, String op, JsonNode jsonNode, List<BatchInput> batchInput, boolean waitabit) {
        // Set the batch status based on waitabit
        BatchStatus status = waitabit ? BatchStatus.BatchWait : BatchStatus.BatchQueued;
        UUID batchId = batchJobService.saveBatch(app, op, jsonNode, batchInput, status);
        return batchId.toString();
    }

    /**
     * Appends inputs to an existing batch.
     *
     * @param batchId     the ID of the batch to append to
     * @param batchInput  the list of batch inputs to append
     * @param waitabit    whether to wait before processing
     * @return a response indicating the success of the operation
     * @throws IllegalArgumentException if the batch ID is invalid, batch status is not "wait",
     *                                  or batchInput contains invalid line numbers
     */
    @Transactional
    public AlyaBatchResponse appendToBatch(String batchId, List<BatchInput> batchInput, boolean waitabit) {
        try {
            // Check if batchInput has at least one entry
            if (batchInput.isEmpty()) {
                throw new IllegalArgumentException("batchInput must have at least one entry");
            }

            // Validate line numbers
            for (BatchInput input : batchInput) {
                if (input.getLine() <= 0) {
                    throw new IllegalArgumentException("all lineno values must be greater than 0");
                }
            }

            // Retrieve batch record from the database
            Batches batch = batchJobService.getBatchByReqId(batchId);
            if (batch == null) {
                throw new IllegalArgumentException("Batch record not found");
            }

            // Check if the status of the batch is wait
            if (!batch.getStatus().equals(BatchStatus.BatchWait)) {
                throw new IllegalArgumentException("Batch status must be wait");
            }

            // Write records to batch rows
            for (BatchInput input : batchInput) {
                batchJobService.saveBatchRow(batch, input.getLine(), input.getInput());
            }

            // Change the status of the batch record if not waiting
            if (!waitabit) {
                batch.setStatus(BatchStatus.BatchQueued);
                batchJobService.saveBatch(batch);
            }

            return new AlyaBatchResponse(batchId, batchInput.size());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while appending to the batch: " + e.getMessage(), e);
        }
    }
}
