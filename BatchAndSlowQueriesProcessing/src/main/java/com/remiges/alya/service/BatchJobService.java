package com.remiges.alya.service;

import java.io.File;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remiges.alya.config.JobManagerConfig;
import com.remiges.alya.entity.BatchJob;
import com.remiges.alya.entity.BatchRows;
import com.remiges.alya.entity.Batches;
import com.remiges.alya.jobs.AlyaConstant;
import com.remiges.alya.jobs.BatchInput;
import com.remiges.alya.jobs.BatchOutput;
import com.remiges.alya.jobs.BatchStatus;
import com.remiges.alya.jobs.ProcessOutputToFle;
import com.remiges.alya.jobs.SummaryUtils;
import com.remiges.alya.repository.BatchRowsRepo;
import com.remiges.alya.repository.BatchesRepo;

import jakarta.transaction.Transactional;

/**
 * Service class for managing batch jobs and their statuses.
 */
@Service
public class BatchJobService {

	private static final Logger logger = LoggerFactory.getLogger(BatchJobService.class);

	private final BatchRowsRepo batchRowRepo;
	private final BatchesRepo batchesRepo;
	private final MinioService minioService;

	private JedisService jedisService;

	JobManagerConfig mgrConfig;

	/**
	 * Constructs a new BatchJobService.
	 *
	 * @param jedisService the Jedis service for Redis operations
	 * @param mgrConfig    the configuration for job management
	 * @param minioService the Minio service for file storage
	 * @param mapper       the ObjectMapper for JSON processing
	 * @param batchRowRepo the repository for batch rows
	 * @param batchesRepo  the repository for batches
	 */
	@Autowired
	public BatchJobService(JedisService jedisService, JobManagerConfig mgrConfig, MinioService minioService,
			ObjectMapper mapper, BatchRowsRepo batchRowRepo, BatchesRepo batchesRepo) {
		this.batchesRepo = batchesRepo;
		this.batchRowRepo = batchRowRepo;
		this.minioService = minioService;
		this.jedisService = jedisService;
		this.mgrConfig = mgrConfig;

	}

	public int getNrowsByBatchId(UUID batchId) {
		// Assuming BatchRowRepository has a method to find nrows by batchId
		List<BatchRows> batchRows = batchRowRepo.findByBatchId(batchId);
		return batchRows.size(); // Assuming you want to count the number of rows
	}

	/**
	 * Counts the number of rows in batchrows associated with a batch.
	 *
	 * @param batch The batch for which to count the rows.
	 * @return The total count of rows in batchrows associated with the batch.
	 */
	@Transactional
	public int countBatchRowsByBatch(Batches batch) {
		return batchRowRepo.countBatchRowsByBatch(batch);
	}

	/**
	 * Saves a batch into the database.
	 *
	 * @param batch the batch to save
	 * @return the saved batch
	 */
	@Transactional
	public Batches saveBatch(Batches batch) {
		return batchesRepo.save(batch);
	}

	/**
	 * Saves a batch row into the database.
	 *
	 * @param batch  the batch associated with the row
	 * @param lineNo the line number of the row
	 * @param input  the input data of the row
	 */
	@Transactional
	public void saveBatchRow(Batches batch, int lineNo, String input) {
		BatchRows batchRow = new BatchRows();
		batchRow.setBatch(batch);
		batchRow.setLine(lineNo);
		batchRow.setInput(input.toString());
		batchRowRepo.save(batchRow);
	}

	/**
	 * Finds batches by type, application, operation, and request time after a
	 * specified threshold time.
	 *
	 * @param type          the type of batch
	 * @param app           the application name
	 * @param op            the operation name
	 * @param thresholdTime the threshold time
	 * @return a list of batches matching the criteria
	 */
	public List<Batches> findBatchesByType(char type, String app, String op, LocalDateTime thresholdTime) {
		return batchesRepo.findByTypeAndAppAndOpAndReqatAfter(type, app, op, thresholdTime);
	}

	/**
	 * Finds batches by application, operation, and request time after a specified
	 * threshold time.
	 *
	 * @param app           the application name
	 * @param op            the operation name
	 * @param thresholdTime the threshold time
	 * @return a list of batches matching the criteria
	 */
	public List<Batches> findBatchesByAppAndOpAndReqAtAfter(String app, String op, LocalDateTime thresholdTime) {
		return batchesRepo.findByAppAndOpAndReqatAfter(app, op, thresholdTime);
	}

	/**
	 * Aborts a batch and its associated rows.
	 *
	 * @param batch the batch to abort
	 * @throws Exception if the batch type is invalid
	 */
	@Transactional
	public void abortBatchAndRows(Batches batch) throws Exception {
		if (batch.getType() == AlyaConstant.TYPE_Q) {
			batch.setStatus(BatchStatus.BatchAborted);
			batch.setDoneat(new Timestamp(System.currentTimeMillis()));
			batchesRepo.save(batch);

			List<BatchRows> batchRows = batchRowRepo.findByBatch(batch, null);
			for (BatchRows batchRow : batchRows) {
				batchRow.setBatchStatus(BatchStatus.BatchAborted);
				batchRow.setDoneat(new Timestamp(System.currentTimeMillis()));
				batchRowRepo.save(batchRow);
			}

			String redisKey = "ALYA_BATCHSTATUS_" + batch.getId().toString();
			jedisService.setRedisStatusSlowQuery(redisKey, BatchStatus.BatchAborted.name());
		} else {
			throw new Exception("Invalid batch type. Only 'Q' type batches can be aborted.");
		}
	}

	/**
	 * Retrieves a batch by request ID.
	 *
	 * @param reqID the request ID
	 * @return the batch
	 * @throws Exception if the batch is not found
	 */
	public Batches getBatchByReqId(String reqID) throws Exception {
		Optional<Batches> batch = batchesRepo.findById(UUID.fromString(reqID));
		if (batch.isPresent()) {
			return batch.get();
		} else {
			throw new Exception("Batch not found for request ID: " + reqID);
		}
	}

	/**
	 * Retrieves the status of a batch by request ID.
	 *
	 * @param reqID the request ID
	 * @return the batch status
	 */
	public BatchStatus getSlowQueryStatusByReqId(String reqID) {
		List<Batches> lstbatch = batchesRepo.findByIdAndType(UUID.fromString(reqID), AlyaConstant.TYPE_Q);

		if (lstbatch.size() > 0) {

			return lstbatch.get(0).getStatus();

		} else {
			return null;
		}
	}

	/**
	 * 
	 * 
	 * 
	 * Updates the status of a batch row.
	 *
	 * @param rowId     the ID of the row
	 * @param newStatus the new status
	 * @throws Exception if the row ID is invalid or the row is not found
	 */
	@Transactional
	public void updateBatchRowStatus1(Long rowId, BatchStatus newStatus) throws Exception {
		Optional<BatchRows> batchRowOptional = batchRowRepo.findById(rowId);
		if (batchRowOptional.isPresent()) {
			BatchRows batchRow = batchRowOptional.get();
			batchRow.setBatchStatus(newStatus);
			batchRowRepo.save(batchRow);
		} else {
			throw new Exception("Batch row with ID " + rowId + " not found.");
		}
	}

	/**
	 * Retrieves the batch row for the specified request ID.
	 *
	 * @param reqId the request ID
	 * @return the batch row, or null if not found
	 */
	public BatchRows getBatchRowByReqId(String reqId) {
		Optional<Batches> batch = batchesRepo.findById(UUID.fromString(reqId));
		if (batch.isPresent()) {
			List<BatchRows> batchRowsList = batchRowRepo.findByBatch(batch.get(), null);
			if (!batchRowsList.isEmpty()) {
				return batchRowsList.get(0);
			}
		}
		return null;
	}

	/**
	 * Saves slow queries into the database.
	 *
	 * @param app     the application name
	 * @param op      the operation name
	 * @param context the context JSON
	 * @param input   the input JSON
	 * @param status  the batch status
	 * @return the ID of the saved batch
	 */
	@Transactional
	public UUID saveSlowQueries(String app, String op, JsonNode context, JsonNode input, BatchStatus status) {
		try {
			if (context == null || input == null) {
				throw new IllegalArgumentException("Context or input is null");
			}

			Batches batchJob = new Batches();
			batchJob.setApp(app);
			batchJob.setOp(op);
			batchJob.setContext(context);
			batchJob.setStatus(status);
			batchJob.setType(AlyaConstant.TYPE_Q);
			batchJob.setReqat(new Timestamp(System.currentTimeMillis()));

			List<BatchRows> batchRowsList = new ArrayList<>();

			if (input.size() > 0) {
				BatchRows batchRow = new BatchRows();
				batchRow.setBatch(batchJob);
				batchRow.setBatchStatus(status);
				batchRow.setLine(1);
				batchRow.setReqat(new Timestamp(System.currentTimeMillis()));
				batchRow.setInput(input.toString());
				batchRowsList.add(batchRow);
			}

			if (!batchRowsList.isEmpty()) {
				// Save batch job and its rows in a single operation
				batchesRepo.save(batchJob);
				batchRowRepo.saveAll(batchRowsList);
			}

			return batchJob.getId();
		} catch (IllegalArgumentException e) {
			logger.error("Error saving slow queries: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("An error occurred while saving the slow queries: " + e.getMessage());
			throw new RuntimeException("An error occurred while saving the slow queries: " + e.getMessage());
		}
	}

	/**
	 * Saves batch jobs into the database.
	 *
	 * @param app        the application name
	 * @param op         the operation name
	 * @param context    the context JSON
	 * @param batchInput the list of batch inputs
	 * @param status     the batch status
	 * @return the ID of the saved batch
	 */
	@Transactional
	public UUID saveBatch(String app, String op, JsonNode context, List<BatchInput> batchInput, BatchStatus status) {
		try {
			Batches batchJob = new Batches();
			batchJob.setApp(app);
			batchJob.setOp(op);
			batchJob.setContext(context);
			batchJob.setStatus(status);
			batchJob.setType(AlyaConstant.TYPE_B);
			batchJob.setReqat(new Timestamp(System.currentTimeMillis()));

			List<BatchRows> batchRowsList = new ArrayList<>();

			batchInput.forEach(batch -> {
				BatchRows batchRow = new BatchRows();
				batchRow.setBatch(batchJob);
				batchRow.setBatchStatus(status);
				batchRow.setInput(batch.getInput());
				batchRow.setLine(batch.getLine());
				batchRow.setReqat(new Timestamp(System.currentTimeMillis()));
				batchRowsList.add(batchRow);
			});

			if (!batchRowsList.isEmpty()) {
				// Save batch job and its rows in a single operation
				batchesRepo.save(batchJob);
				batchRowRepo.saveAll(batchRowsList);
			}

			return batchJob.getId();
		} catch (Exception e) {
			logger.error("An error occurred while saving the batch: " + e.getMessage());
			throw new RuntimeException("An error occurred while saving the batch: " + e.getMessage());
		}
	}

	/**
	 * Retrieves all queued batch rows with the specified status.
	 *
	 * @param status the status to filter batch rows
	 * @return a list of batch jobs
	 */
	@Transactional
	public List<BatchJob> getAllQueuedBatchRows(BatchStatus status) {

		int limit = mgrConfig.getALYA_BATCHCHUNK_NROWS();
		Pageable pageable = PageRequest.of(0, limit);

		return batchRowRepo.findAllWithBatches(status, pageable);

	}

	@Transactional
	public void updateBatchRowStatus(Long rowId, BatchStatus batchStatus) throws Exception {
		Optional<BatchRows> batchRow = batchRowRepo.findById(rowId);
		if (batchRow.isPresent()) {
			batchRow.get().setBatchStatus(batchStatus);
			batchRowRepo.save(batchRow.get());
		} else {
			throw new Exception("Batch row not found for ID: " + rowId);
		}
	}

	/**
	 * Updates the status of a batch.
	 *
	 * @param batchId the ID of the batch to update
	 * @param status  the new status
	 * @throws Exception if the batch is not found
	 */
	@Transactional
	public void updateBatchStatus(UUID batchId, BatchStatus status) throws Exception {
		Optional<Batches> batch = batchesRepo.findById(batchId);
		if (batch.isPresent()) {
			batch.get().setStatus(status);
			batchesRepo.save(batch.get());
		} else {
			throw new Exception("Batch not found for ID: " + batchId);
		}
	}

	/**
	 * Updates the summary of a batch.
	 *
	 * @param batchId     the ID of the batch to update
	 * @param Status      the new status
	 * @param nfailed     the number of failed rows
	 * @param nsuccess    the number of successful rows
	 * @param nabort      the number of aborted rows
	 * @param outputfiles the output files
	 * @throws Exception if the batch is not found
	 */
	public void UpdateBatchSummary(UUID batchId, BatchStatus Status, Integer nfailed, Integer nsuccess, Integer nabort,
			Map<String, String> outputfiles) throws Exception {
		Optional<Batches> batchrec = batchesRepo.findById(batchId);
		if (!batchrec.isPresent()) {
			String erlog = "batch not found for batchid" + batchId;
			logger.debug(erlog);
			throw new Exception(erlog);
		}

		Batches batch = batchrec.get();
		batch.setStatus(Status);
		batch.setDoneat(Timestamp.from(Instant.now()));
		batch.setNaborted(nabort);
		batch.setNfailed(nfailed);
		batch.setNsuccess(nsuccess);
		batch.setOutputfiles(outputfiles);

		batchesRepo.save(batch);
	}

	/**
	 * Switches the status of batch rows and batches to in-progress.
	 *
	 * @param queuedbatchrows the list of queued batch rows
	 * @param batchIds        the set of batch IDs
	 * @param status          the new status
	 * @throws Exception if updating the status fails
	 */
	@Transactional
	public void SwitchBatchToInprogress(List<BatchJob> queuedbatchrows, Set<UUID> batchIds, BatchStatus status)
			throws Exception {
		for (BatchJob batchrow : queuedbatchrows) {
			try {
				updateBatchRowStatus(batchrow.getRowId(), status);
			} catch (Exception e) {
				String erlog = "exception while updating batchrow status ex = " + e.getMessage();
				logger.debug(erlog);
				throw e;
			}
		}

		for (UUID batchId : batchIds) {
			try {
				updateBatchStatus(batchId, status);
			} catch (Exception e) {
				String erlog = "exception while updating batch status ex = " + e.getMessage();
				logger.debug(erlog);
				throw e;
			}
		}
	}

	/**
	 * Updates the batch row with the results of a slow query.
	 *
	 * @param rowtoproces the batch job to update
	 * @param batchOutput the batch output
	 */
	public void updateBatchRowForSlowQueryoutput(BatchJob rowtoproces, BatchOutput batchOutput) {
		Optional<BatchRows> sqRow = batchRowRepo.findById(rowtoproces.getRowId());
		if (sqRow.isPresent()) {
			BatchRows batchRows = sqRow.get();
			batchRows.setDoneat(Timestamp.from(Instant.now()));
			batchRows.setBatchStatus(batchOutput.getStatus());
			batchRows.setRes(batchOutput.getResult());
			batchRows.setMessages(batchOutput.getMessages());
			batchRowRepo.save(batchRows);
		}
	}

	/**
	 * Updates the batch row with the results of a batch job.
	 *
	 * @param rowtoproces the batch job to update
	 * @param batchOutput the batch output
	 */
	public void updateBatchRowForBatchOutput(BatchJob rowtoproces, BatchOutput batchOutput) {
		Optional<BatchRows> sqRow = batchRowRepo.findById(rowtoproces.getRowId());
		if (sqRow.isPresent()) {
			BatchRows batchRows = sqRow.get();
			batchRows.setDoneat(Timestamp.from(Instant.now()));
			batchRows.setBatchStatus(batchOutput.getStatus());
			batchRows.setRes(batchOutput.getResult());
			batchRows.setMessages(batchOutput.getMessages());
			batchRows.setBlobrows(batchOutput.getBlobRows());
			batchRowRepo.save(batchRows);
		}
	}

	/**
	 * Retrieves the batch rows for the specified batch, sorted by line.
	 *
	 * @param batch the batch to retrieve rows for
	 * @return the list of batch rows
	 */
	public List<BatchRows> GetBatchRowsByBatchIDSorted(Batches batch) {
		return batchRowRepo.findByBatch(batch, Sort.by(Sort.Direction.ASC, "line"));
	}

	/**
	 * Summarizes the batch if it is completed. Determines the number of batch rows
	 * that succeeded, failed, or were aborted, and updates the batch status
	 * accordingly.
	 *
	 * @param batchId the batch ID to summarize
	 * @return a message indicating the result of the operation
	 */
	@Transactional
	public String SummarizeBatch(UUID batchId) {
		Optional<Batches> opbatch = batchesRepo.findById(batchId);
		if (!opbatch.isPresent()) {
			return "Batch not found";
		}

		Batches batch = opbatch.get();

		// some other thread is summarizing or has summarized this batch return
		if (batch.getDoneat() != null) {
			return "";
		}

		Integer count = batchRowRepo.countBatchRowsByBatchIDAndStatus(batch,
				List.of(BatchStatus.BatchInProgress, BatchStatus.BatchQueued));

		if (count > 0) {
			// the batch is not yet complete, some rows are pending
			return "";
		}

		// Fetch all batch rows records for the batch, sorted by "line"
		List<BatchRows> batchrows = batchRowRepo.findByBatch(batch, Sort.by(Sort.Direction.ASC, "line"));

		SummaryUtils sumutils = new SummaryUtils();
		sumutils.calculateSummaryCounters(batchrows);
		sumutils.determineBatchStatus();

		List<BatchRows> processedBatchRows = batchRowRepo.GetProcessedBatchRowsByBatchIDSortedRow(batch,
				List.of(BatchStatus.BatchSuccess, BatchStatus.BatchFailed));

		ProcessOutputToFle outprocessor = new ProcessOutputToFle();
		try {
			// Create temporary files for each unique logical file in blob rows
			outprocessor.GenerateTempFile(processedBatchRows);

			// Append blob rows strings to the appropriate temporary files
			outprocessor.appendBlobRowsToFiles(processedBatchRows);
		} catch (Exception ex) {
			logger.debug("Failed to process blob row to files {}", ex);
			return "Failed to process blob row to files " + ex.toString();
		}

		Map<String, File> outputFilemap = outprocessor.getOutputFilemap();

		Map<String, String> outputfiles;
		try {
			// Move files to object store and update output files map
			outputfiles = moveFilesToObjectStore(outputFilemap);

			outprocessor.cleanupTemporaryFiles();

		} catch (Exception ex) {
			logger.debug("Failed to store files to object store", ex);
			return "Failed to store files to object store " + ex.toString();
		}

		// Update the batches record with summarized information
		try {
			UpdateBatchSummary(batchId, sumutils.getSummaryStatus(), sumutils.getNfailed(), sumutils.getNSuccess(),
					sumutils.getNaborted(), outputfiles);
		} catch (Exception e) {
			String erlog = "exception while updating batch summary for batch id : " + batchId + " ex = "
					+ e.getMessage();
			logger.debug(erlog);
			return erlog;
		}

		jedisService.updateStatusInRedis(batchId, sumutils.getSummaryStatus(),
				mgrConfig.getALYA_BATCHSTATUS_CACHEDUR_SEC() * 100);

		return "";
	}

	/**
	 * Moves the generated files to the object store and returns a map of filenames
	 * to their new storage locations.
	 *
	 * @param outputFilemap the map of filenames to files
	 * @return a map of filenames to their storage locations
	 * @throws Exception if an error occurs during file storage
	 */
	public Map<String, String> moveFilesToObjectStore(Map<String, File> outputFilemap) throws Exception {
		Map<String, String> outputfiles = new HashMap<>();

		for (Map.Entry<String, File> entry : outputFilemap.entrySet()) {
			String filename = entry.getKey();
			File file = entry.getValue();

			try {
				UUID randomUUID = UUID.randomUUID();
				// Move temporary files to the object store and update output files
				minioService.uploadFile(randomUUID.toString(), file, "text/plain");
				outputfiles.put(filename, randomUUID.toString());
			} catch (Exception ex) {
				String erlog = "exception while storing file for ex = " + ex.getMessage();
				logger.debug(erlog);
				throw ex;
			}
		}
		return outputfiles;
	}
}
