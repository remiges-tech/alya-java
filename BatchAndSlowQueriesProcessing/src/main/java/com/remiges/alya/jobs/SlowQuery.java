package com.remiges.alya.jobs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.config.JobManagerConfig;
import com.remiges.alya.entity.BatchRows;
import com.remiges.alya.entity.Batches;
import com.remiges.alya.jobs.SlowQueryResult.AlyaErrorMessage;
import com.remiges.alya.service.BatchJobService;
import com.remiges.alya.service.JedisService;

/**
 * Component responsible for managing slow queries.
 */
@Component
public class SlowQuery {

	private final BatchJobService batchJobService;
	private final JedisService jedissrv;

	/**
	 * Constructs a SlowQuery instance.
	 *
	 * @param batchJobService Batch job service for managing batch jobs.
	 * @param jedissrv        Jedis service for managing Redis operations.
	 * @param mgrconfig       Configuration for job manager.
	 */
	@Autowired
	public SlowQuery(BatchJobService batchJobService, JedisService jedissrv, JobManagerConfig mgrconfig) {
		this.batchJobService = batchJobService;
		this.jedissrv = jedissrv;
	}

	/**
	 * Submits a slow query job.
	 *
	 * @param app     Application name.
	 * @param op      Operation name.
	 * @param context Context JSON node.
	 * @param input   Input JSON node.
	 * @return Unique request ID if successful, error message otherwise.
	 */
	public String submit(String app, String op, JsonNode context, JsonNode input) {
		try {
			UUID reqID = batchJobService.SaveSlowQueries(app, op, context, input, BatchStatus.BatchQueued);
			return reqID != null ? reqID.toString() : null;
		} catch (Exception e) {
			e.printStackTrace();
			return "An error occurred while submitting the job: " + e.getMessage();
		}
	}

	/**
	 * Processes the completion of a slow query job.
	 *
	 * @param reqID Request ID of the job.
	 * @return Result of the job processing.
	 */
	public SlowQueryResult done(String reqID) {
		// Initialization
		BatchStatus status = null;
		List<AlyaErrorMessage> messages = new ArrayList<>();
		Map<String, String> outputFiles = new HashMap<>();
		Exception error = null;

		try {
			// Check REDIS for the status entry
			String redisKey = "ALYA_BATCHSTATUS_" + reqID;
			String redisValue = jedissrv.getBatchStatusFromRedis(redisKey);
			if (redisValue != null) {
				status = jedissrv.getBatchStatus(redisValue);
				if (status != BatchStatus.BatchTryLater) {
					return new SlowQueryResult(status, null, messages, outputFiles, error);
				}
			}

			// Check database for status
			BatchStatus dbStatus = batchJobService.getBatchStatusFromBatches(reqID);
			if (dbStatus == BatchStatus.BatchSuccess || dbStatus == BatchStatus.BatchFailed) {
				BatchRows batchRow = batchJobService.getBatchRowByReqId(reqID);
				if (batchRow != null) {
					status = batchRow.getBatchStatus();
					batchJobService.updateBatchRowStatus(batchRow.getRowId(), status);
				}
			} else if (dbStatus == BatchStatus.BatchAborted) {
				return new SlowQueryResult(BatchStatus.BatchAborted, null, null, null, null);
			} else {
				// Insert new REDIS record
				jedissrv.setRedisStatusSlowQuery(redisKey, BatchStatus.BatchTryLater);
				status = BatchStatus.BatchTryLater;
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = e;
		}

		return new SlowQueryResult(status, null, messages, outputFiles, error);
	}

	/**
	 * Aborts a slow query job.
	 *
	 * @param reqID Request ID of the job.
	 * @throws Exception If an error occurs during job abortion.
	 */
	public void abort(String reqID) throws Exception {
		// Fetch the batch job associated with the reqID
		Batches batch = batchJobService.getBatchByReqId(reqID);
		if (batch == null) {
			throw new Exception("Invalid request ID");
		}

		// Validate batch for abortion
		char type = batch.getType();
		BatchStatus status = batch.getStatus();
		if (type != 'Q') {
			throw new Exception("Batch type is not 'Q'");
		}
		if (status == BatchStatus.BatchAborted || status == BatchStatus.BatchSuccess
				|| status == BatchStatus.BatchFailed) {
			throw new Exception("Cannot abort batch with status " + status);
		}

		// Update batch and batch rows records
		batchJobService.abortBatchAndRows(batch);

		// Update REDIS batch status record
		jedissrv.updateStatusInRedis(UUID.fromString(reqID), BatchStatus.BatchAborted,
				jedissrv.ALYA_BATCHSTATUS_CACHEDUR_SEC * 100);
	}

	/**
	 * Lists slow queries based on application, operation, and age.
	 *
	 * @param app Application name.
	 * @param op  Operation name.
	 * @param age Age of the queries in days.
	 * @return List of slow queries.
	 */
	public List<SlowQueriesResultList> list(String app, String op, int age) {
		List<SlowQueriesResultList> slowQueries = new ArrayList<>();

		// Calculate threshold time based on age
		LocalDateTime thresholdTime = LocalDateTime.now().minusDays(age);

		// Retrieve slow queries from repository
		List<Batches> batchRowsList = batchJobService.findByAppAndOpAndReqatAfter(app, op, thresholdTime);

		// Map BatchRows to SlowQueryDetails_t
		for (Batches batch : batchRowsList) {
			SlowQueriesResultList slowQuery = new SlowQueriesResultList();
			slowQuery.setId(batch.getId().toString());
			slowQuery.setApp(batch.getApp());
			slowQuery.setOp(batch.getOp());
			slowQuery.setInputfile(batch.getInputfile());
			slowQuery.setStatus(batch.getStatus());
			slowQuery.setReqat(batch.getReqat());
			slowQuery.setDoneat(batch.getDoneat());
			slowQuery.setOutputfiles(null); // Output files mapping not available in BatchRows entity

			slowQueries.add(slowQuery);
		}

		return slowQueries;
	}
}
