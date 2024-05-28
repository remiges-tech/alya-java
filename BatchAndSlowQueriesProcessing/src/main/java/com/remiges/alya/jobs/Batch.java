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
	 * @param app        the application name
	 * @param op         the operation name
	 * @param jsonNode   the JSON context
	 * @param batchInput the list of batch inputs
	 * @param waitabit   whether to wait before processing
	 * @return the ID of the submitted batch
	 */
	public String submitBatch(String app, String op, JsonNode jsonNode, List<BatchInput> batchInput, boolean waitabit) {
		// Set the batch status based on waitabit
		BatchStatus status = waitabit ? BatchStatus.BatchWait : BatchStatus.BatchQueued;
		UUID batchId = batchJobService.saveBatch(app, op, jsonNode, batchInput, status);
		return batchId.toString();
	}

	/**
	 * Appends additional rows to an existing batch after it has been submitted.
	 * 
	 * @param batchId    The unique identifier of the batch to which the rows are
	 *                   appended.
	 * @param batchInput An array containing the data for all the rows to be
	 *                   appended to the batch. Each element of the array consists
	 *                   of a line number and corresponding JSON input. Line numbers
	 *                   must be greater than 0.
	 * @param waitABit   Indicates whether the batch is fully submitted (false) or
	 *                   to be held back for further processing (true). If false, it
	 *                   indicates that this is the last round of append and the
	 *                   batch is ready for immediate processing. If true, it
	 *                   signifies that the batch is not yet fully submitted or
	 *                   needs to be held back.
	 * 
	 * @return Returns an AlyaBatchResponse containing the batch ID and the total
	 *         count of rows appended.
	 * 
	 * @throws IllegalArgumentException If batchId is not found, batchInput is
	 *                                  empty, or line numbers are not greater than
	 *                                  0.
	 * @throws BatchAppendException     If an error occurs while appending rows to
	 *                                  the batch.
	 */
	@Transactional
	public AlyaBatchResponse appendToBatch(String batchId, List<BatchInput> batchInput, boolean waitABit) {
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
			if (!waitABit) {
				batch.setStatus(BatchStatus.BatchQueued);
				batchJobService.saveBatch(batch);
			}

			// Return AlyaBatchResponse
			return new AlyaBatchResponse(batchId, batchInput.size());
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("An error occurred while appending to the batch: " + e.getMessage(), e);
		}
	}

	/**
	 * Sets the status of a batch from 'wait' to 'queued'.
	 *
	 * @param batchId The unique identifier of the batch.
	 * @return Returns the batch ID and the total count of rows in batchrows against
	 *         this batch if successful.
	 * @throws RuntimeException If the batch record does not exist, has a status
	 *                          other than 'wait' or 'queued', or an error occurs
	 *                          during the process.
	 */
	public AlyaBatchResponse waitOff(String batchId) {
		Batches batch = null;
		try {
			// Retrieve batch record from the database
			batch = batchJobService.getBatchByReqId(batchId);

			// Check if the batch record exists and its status is 'wait'
			if (batch == null) {
				throw new IllegalArgumentException("Batch record not found");
			} else if (!batch.getStatus().equals(BatchStatus.BatchWait)) {
				throw new IllegalArgumentException("Batch status must be 'wait'");
			}

			// Change the status of the batch record to 'queued'
			batch.setStatus(BatchStatus.BatchQueued);
			batchJobService.saveBatch(batch);

			// Get the total number of rows in batchrows against this batch
			int numberOfRows = batchJobService.countBatchRowsByBatch(batch);

			// Return AlyaBatchResponse
			return new AlyaBatchResponse(batchId, numberOfRows);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(
					"An error occurred while setting the status of the batch from 'wait' to 'queued': "
							+ e.getMessage(),
					e);
		}
	}

}
