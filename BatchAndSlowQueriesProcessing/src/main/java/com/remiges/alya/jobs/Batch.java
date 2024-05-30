package com.remiges.alya.jobs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.config.JobManagerConfig;
import com.remiges.alya.entity.Batches;
import com.remiges.alya.service.BatchJobService;
import com.remiges.alya.service.JedisService;

import jakarta.transaction.Transactional;

/**
 * Component for managing batch operations.
 */
@Component
public class Batch {

	private final BatchJobService batchJobService;
	private final JedisService jedissrv;
	private final Logger logger;

	/**
	 * Constructs a SlowQuery instance.
	 *
	 * @param batchJobService Batch job service for managing batch jobs.
	 * @param jedissrv        Jedis service for managing Redis operations.
	 * @param mgrconfig       Configuration for job manager.
	 */
	@Autowired
	public Batch(BatchJobService batchJobService, JedisService jedissrv, JobManagerConfig mgrconfig, Logger logger) {
		this.batchJobService = batchJobService;
		this.jedissrv = jedissrv;
		this.logger = Logger.getLogger(SlowQuery.class.getName());
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
	public String submit(String app, String op, JsonNode jsonNode, List<BatchInput> batchInput, boolean waitabit) {
		try {
			// Set the batch status based on waitabit
			BatchStatus status = waitabit ? BatchStatus.BatchWait : BatchStatus.BatchQueued;
			UUID batchId = batchJobService.saveBatch(app, op, jsonNode, batchInput, status);
			return batchId.toString();
		} catch (Exception e) {
			logger.severe("Error occurred while submitting the batch: " + e.getMessage());
			e.printStackTrace();
			return "An error occurred while submitting the batch: " + e.getMessage();
		}

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
	public AlyaBatchResponse append(String batchId, List<BatchInput> batchInput, boolean waitABit) {
		// Check if batchInput has at least one entry
		if (batchInput.isEmpty()) {
			throw new IllegalArgumentException("batchInput must have at least one entry");
		}

		// Validate line numbers
		if (batchInput.stream().anyMatch(input -> input.getLine() <= 0)) {
			throw new IllegalArgumentException("all lineno values must be greater than 0");
		}

		try {
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
			batchInput.forEach(input -> batchJobService.saveBatchRow(batch, input.getLine(), input.getInput()));

			// Change the status of the batch record if not waiting
			if (!waitABit) {
				batch.setStatus(BatchStatus.BatchQueued);
				batchJobService.saveBatch(batch);
			}

			// Return AlyaBatchResponse
			return new AlyaBatchResponse(batchId, batchInput.size());
		} catch (Exception e) {
			// Log the error for debugging and rethrow as a more generic exception
			logger.severe("An error occurred while appending to the batch: " + e.getMessage());
			throw new RuntimeException("An error occurred while appending to the batch");
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
	    Logger logger = Logger.getLogger(AlyaBatchResponse.class.getName());
	    try {
	        Batches batch = batchJobService.getBatchByReqId(batchId);

	        // Check if the batch record exists and its status is 'wait'
	        if (batch == null) {
	            throw new IllegalArgumentException("Batch record not found");
	        } else if (batch.getStatus() != BatchStatus.BatchWait) {
	            throw new IllegalArgumentException("Batch status must be 'wait'");
	        }

	        // Change the status of the batch record to 'queued' and save
	        batch.setStatus(BatchStatus.BatchQueued);
	        batchJobService.saveBatch(batch);

	        // Get the total number of rows in batchrows against this batch
	        int numberOfRows = batchJobService.countBatchRowsByBatch(batch);

	        // Return AlyaBatchResponse
	        return new AlyaBatchResponse(batchId, numberOfRows);
	    } catch (IllegalArgumentException e) {
	        // Log the exception with a warning level
	        logger.severe("IllegalArgumentException occurred: " + e.getMessage());
	        throw e; // Rethrow the exception
	    } catch (Exception e) {
	        // Log the exception with an error level
	        logger.severe("An error occurred while setting the status of the batch from 'wait' to 'queued': " + e.getMessage());
	        throw new RuntimeException("An error occurred while setting the status of the batch from 'wait' to 'queued'", e);
	    }
	}

	/**
	 * List all the batches currently being processed or processed in the past
	 * against a specific app.
	 *
	 * @param app The specific app for which batches will be listed.
	 * @param op  Optional operation name to filter batches.
	 * @param age The age of the records to return, in days.
	 * @return Returns an array of BatchDetails_t objects representing the batches.
	 */
	public List<BatchResultDTO> list(String app, String op, int age) {
		try {
			// Calculate the threshold time based on the age parameter
			LocalDateTime thresholdTime = LocalDateTime.now().minusDays(age);

			// Query the database for matching batches
			List<Batches> matchingBatches = batchJobService.findBatchesByType(AlyaConstant.TYPE_B, app, op,
					thresholdTime);

			// Construct BatchDetails_t objects for each matching batch
			List<BatchResultDTO> batchDetailsList = new ArrayList<>();
			matchingBatches.forEach(batch -> {
				BatchResultDTO batchDetails = new BatchResultDTO();
				batchDetails.setId(batch.getId().toString());
				batchDetails.setApp(batch.getApp());
				batchDetails.setOp(batch.getOp());
				batchDetails.setInputfile(""); // You may set this based on your business logic
				batchDetails.setStatus(batch.getStatus());
				batchDetails.setReqat(batch.getReqat().toLocalDateTime());
				batchDetails.setDoneat(batch.getDoneat() != null ? batch.getDoneat().toLocalDateTime() : null);
				batchDetails.setOutputfiles(batch.getOutputfiles());
				batchDetails.setNsuccess(batch.getNsuccess());
				batchDetails.setNfailed(batch.getNfailed());
				batchDetails.setNaborted(batch.getNaborted());

				// Fetch nrows from batchrows table and set it in the DTO
				int nrows = batchJobService.getNrowsByBatchId(batch.getId()); // Assuming you have a method to fetch
																				// nrows by batch ID
				batchDetails.setNrows(nrows);

				batchDetailsList.add(batchDetails);
			});

			return batchDetailsList;
		} catch (Exception e) {
			throw new RuntimeException("An error occurred while listing batches: " + e.getMessage(), e);
		}
	}

	public void abort(String batchId) throws Exception {
		try {
			String redisKey = "ALYA_BATCHSTATUS_" + batchId;
			String redisValue = jedissrv.getBatchStatusFromRedis(redisKey);
			BatchStatus status = (redisValue != null) ? jedissrv.getBatchStatus(redisValue)
					: batchJobService.getBatchStatusByReqId(batchId);

			if (status == BatchStatus.BatchAborted || status == BatchStatus.BatchSuccess
					|| status == BatchStatus.BatchFailed) {
				throw new Exception("Cannot abort batch with status " + status);
			}

			Batches batch = batchJobService.getBatchByReqId(batchId);
			batchJobService.abortBatchAndRows(batch);

			jedissrv.updateStatusInRedis(UUID.fromString(batchId), BatchStatus.BatchAborted,
					jedissrv.ALYA_BATCHSTATUS_CACHEDUR_SEC * 100);
			logger.info("Batch with request ID " + batchId + " aborted successfully.");
		} catch (Exception e) {
			logger.severe("Error occurred while aborting the batch: " + e.getMessage());
			throw e;
		}
	}

}
