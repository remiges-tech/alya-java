package com.remiges.alya.service;

import java.io.File;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remiges.alya.config.JobManagerConfig;
import com.remiges.alya.entity.BatchJob;
import com.remiges.alya.entity.BatchRows;
import com.remiges.alya.entity.Batches;
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

	private final ObjectMapper mapper;
	private final BatchRowsRepo batchrowrepo;
	private final BatchesRepo batchesRepo;
	private final MinioService minioSrv;

	private JedisService jedissrv;

	JobManagerConfig mgrConfig;

	public BatchStatus getBatchStatusFromBatches(String reqID) {
		return batchesRepo.findByReqId(reqID).getStatus();
	}

	@Transactional
	public void updateBatchRowStatus(Long rowId, BatchStatus newStatus) throws Exception {
		if (rowId != null) {
			Optional<BatchRows> batchRowOptional = batchrowrepo.findById(rowId);
			if (batchRowOptional.isPresent()) {
				BatchRows batchRow = batchRowOptional.get();
				batchRow.setBatchStatus(newStatus);
				batchrowrepo.save(batchRow);
			} else {
				throw new Exception("Batch row with ID " + rowId + " not found.");
			}
		} else {
			throw new Exception("Invalid UUID format for row ID: " + rowId);
		}
	}

	/**
	 * Retrieves the batch row for the specified request ID.
	 *
	 * @param reqId the request ID to retrieve the row for
	 * @return the batch row, or null if not found
	 */
	public BatchRows getBatchRowByReqId(String reqId) {
		// First, fetch the corresponding batch from the database based on the reqId
		Optional<Batches> batch = Optional.ofNullable(batchesRepo.findByReqId(reqId));

		// If the batch with the given reqId exists
		if (batch.isPresent()) {
			// Retrieve the list of batch rows associated with the batch
			List<BatchRows> batchRowsList = batchrowrepo.findByBatch(batch.get(), null);

			// If there are batch rows associated with the batch
			if (!batchRowsList.isEmpty()) {
				// Return the first batch row from the list
				return batchRowsList.get(0);
			}
		}

		// If no batch row is found for the given reqId, return null
		return null;
	}

	@Transactional
	public UUID SaveSlowQueries(String app, String op, JsonNode context, JsonNode input, BatchStatus status) {

		Batches batchJob = new Batches();
		batchJob.setApp(app);
		batchJob.setOp(op);
		batchJob.setContext(context); // Convert context to JsonNode
		batchJob.setStatus(status);
		batchJob.setType('Q');
		batchJob.setReqat(new Timestamp(System.currentTimeMillis()));
		Batches savedBatch = batchesRepo.save(batchJob);

		BatchRows btrow = new BatchRows();
		btrow.setBatch(savedBatch);
		btrow.setBatchStatus(status);
		btrow.setInput(input.toString());
		btrow.setReqat(new Timestamp(System.currentTimeMillis()));
		batchrowrepo.save(btrow);

		return savedBatch.getId();
	}

	@Transactional
	public UUID SaveBatch(String app, String op, JsonNode context, List<BatchInput> batchInput, BatchStatus status) {

		Batches batchJob = new Batches();
		batchJob.setApp(app);
		batchJob.setOp(op);
		batchJob.setContext(context);
		batchJob.setStatus(status);
		batchJob.setType('B');
		batchJob.setReqat(new Timestamp(System.currentTimeMillis()));
		Batches savedBatch = batchesRepo.save(batchJob);

		for (BatchInput batch : batchInput) {

			BatchRows btrow = new BatchRows();
			btrow.setBatch(savedBatch);
			btrow.setBatchStatus(status);
			btrow.setInput(batch.getInput());
			btrow.setLine(batch.getLine());
			btrow.setReqat(new Timestamp(System.currentTimeMillis()));
			batchrowrepo.save(btrow);

		}

		return savedBatch.getId();

	}

	/**
	 * Constructs a new BatchJobService.
	 *
	 * @param minioSrv     the Minio service for file storage
	 * @param mapper       the ObjectMapper for JSON processing
	 * @param batchrowrepo the repository for batch rows
	 * @param batchesRepo  the repository for batches
	 */
	@Autowired
	public BatchJobService(JedisService jedissrv, JobManagerConfig mgrconfig, MinioService minioSrv,
			ObjectMapper mapper, BatchRowsRepo batchrowrepo, BatchesRepo batchesRepo) {
		this.batchesRepo = batchesRepo;
		this.batchrowrepo = batchrowrepo;
		this.mapper = mapper;
		this.minioSrv = minioSrv;
		this.jedissrv = jedissrv;
		this.mgrConfig = mgrconfig;
	}

	/**
	 * Retrieves all queued batch rows with the specified status.
	 *
	 * @param status the status to filter batch rows
	 * @return a list of batch jobs
	 */
	@Transactional
	public List<BatchJob> getAllQueuedBatchRow(BatchStatus status) {
		return batchrowrepo.findAllWithBatches(status);
	}

	/**
	 * Updates the status of a batch row.
	 *
	 * @param rowid       the ID of the row to update
	 * @param batchStatus the new status
	 * @throws Exception if the batch row is not found
	 */
	public void UpdateBatchRowStatus(Long rowid, BatchStatus batchStatus) throws Exception {
		Optional<BatchRows> batchrow = batchrowrepo.findById(rowid);
		if (batchrow.isPresent()) {
			batchrow.get().setBatchStatus(batchStatus);
			batchrowrepo.save(batchrow.get());
		} else {
			String erlog = "batch row not found" + rowid;
			logger.debug(erlog);
			throw new Exception(erlog);
		}
	}

	/**
	 * Updates the status of a batch.
	 *
	 * @param batchId the ID of the batch to update
	 * @param Status  the new status
	 * @throws Exception if the batch is not found
	 */
	public void UpdateBatchStatus(UUID batchId, BatchStatus Status) throws Exception {
		Optional<Batches> batchrec = batchesRepo.findById(batchId);
		if (batchrec.isPresent()) {
			batchrec.get().setStatus(Status);
			batchesRepo.save(batchrec.get());
		} else {
			String erlog = "batch not found for batchid" + batchId;
			logger.debug(erlog);
			throw new Exception(erlog);
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
				UpdateBatchRowStatus(batchrow.getRowId(), status);
			} catch (Exception e) {
				String erlog = "exception while updating batchrow status ex = " + e.getMessage();
				logger.debug(erlog);
				throw e;
			}
		}

		for (UUID batchId : batchIds) {
			try {
				UpdateBatchStatus(batchId, status);
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
		Optional<BatchRows> sqRow = batchrowrepo.findById(rowtoproces.getRowId());
		if (sqRow.isPresent()) {
			BatchRows batchRows = sqRow.get();
			batchRows.setDoneat(Timestamp.from(Instant.now()));
			batchRows.setBatchStatus(batchOutput.getStatus());
			batchRows.setRes(batchOutput.getResult());
			batchRows.setMessages(batchOutput.getMessages());
			batchrowrepo.save(batchRows);
		}
	}

	/**
	 * Updates the batch row with the results of a batch job.
	 *
	 * @param rowtoproces the batch job to update
	 * @param batchOutput the batch output
	 */
	public void updateBatchRowForBatchOutput(BatchJob rowtoproces, BatchOutput batchOutput) {
		Optional<BatchRows> sqRow = batchrowrepo.findById(rowtoproces.getRowId());
		if (sqRow.isPresent()) {
			BatchRows batchRows = sqRow.get();
			batchRows.setDoneat(Timestamp.from(Instant.now()));
			batchRows.setBatchStatus(batchOutput.getStatus());
			batchRows.setRes(batchOutput.getResult());
			batchRows.setMessages(batchOutput.getMessages());
			batchRows.setBlobrows(batchOutput.getBlobRows());
			batchrowrepo.save(batchRows);
		}
	}

	/**
	 * Retrieves the batch rows for the specified batch, sorted by line.
	 *
	 * @param batch the batch to retrieve rows for
	 * @return the list of batch rows
	 */
	public List<BatchRows> GetBatchRowsByBatchIDSorted(Batches batch) {
		return batchrowrepo.findByBatch(batch, Sort.by(Sort.Direction.ASC, "line"));
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

		Integer count = batchrowrepo.countBatchRowsByBatchIDAndStatus(batch,
				List.of(BatchStatus.BatchInProgress, BatchStatus.BatchQueued));

		if (count > 0) {
			// the batch is not yet complete, some rows are pending
			return "";
		}

		// Fetch all batch rows records for the batch, sorted by "line"
		List<BatchRows> batchrows = batchrowrepo.findByBatch(batch, Sort.by(Sort.Direction.ASC, "line"));

		SummaryUtils sumutils = new SummaryUtils();
		sumutils.calculateSummaryCounters(batchrows);
		sumutils.determineBatchStatus();

		List<BatchRows> processedBatchRows = batchrowrepo.GetProcessedBatchRowsByBatchIDSortedRow(batch,
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
		} catch (Exception ex) {
			logger.debug("Failed to store files to object store", ex);
			return "Failed to store files to object store " + ex.toString();
		}

		// Update the batches record with summarized information
		try {
			UpdateBatchSummary(batchId, sumutils.getSummaryStatus(), sumutils.getNfailed(), sumutils.getNSuccess(),
					sumutils.getNSuccess(), outputfiles);
		} catch (Exception e) {
			String erlog = "exception while updating batch summary for batch id : " + batchId + " ex = "
					+ e.getMessage();
			logger.debug(erlog);
			return erlog;
		}

		jedissrv.updateStatusInRedis(batchId, sumutils.getSummaryStatus(),
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
				minioSrv.uploadFile(randomUUID.toString(), file, "text/plain");
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
