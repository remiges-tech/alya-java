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
import com.remiges.alya.service.JedisService;;

@Component
public class SlowQuery {

	private BatchJobService batchJobService;
	private boolean bprocessJobs = true;
	private Thread jobprocessoThread = null;
	private JedisService jedissrv;

	@Autowired
	public SlowQuery(BatchJobService batchJobService, JedisService jedissrv, JobManagerConfig mgrconfig) {
		this.batchJobService = batchJobService;
		this.jedissrv = jedissrv;
	}

	public String Submit(String app, String op, JsonNode context, JsonNode input) {
		try {
			if (batchJobService != null) {
				UUID reqID = batchJobService.SaveSlowQueries(app, op, context, input, BatchStatus.BatchQueued);
				return reqID != null ? reqID.toString() : null;
			} else {
				return "batchJobService is null";
			}
		} catch (Exception e) {
			// Log the exception
			e.printStackTrace();
			return "An error occurred while submitting the job: " + e.getMessage();
		}
	}

	public SlowQueryResult Done(String reqID) {
		// Initialize return parameters
		BatchStatus status = null;
		String result = null;
		List<AlyaErrorMessage> messages = new ArrayList<>();
		Map<String, String> outputFiles = new HashMap<>();
		Exception error = null;

		// Check REDIS for the status entry
		String redisKey = "ALYA_BATCHSTATUS_" + reqID;
		String redisValue = jedissrv.getBatchStatusFromRedis(redisKey);

		// If a REDIS record exists
		if (redisValue != null) {
			status = jedissrv.getBatchStatus(redisValue);
			if (status != BatchStatus.BatchTryLater) {
				return new SlowQueryResult(status, result, messages, outputFiles, error);
			}
		}

		// If no REDIS record or BatchTryLater status, look up in database
		BatchStatus dbStatus = batchJobService.getBatchStatusFromBatches(reqID);
		if (dbStatus == BatchStatus.BatchSuccess || dbStatus == BatchStatus.BatchFailed) {
			BatchRows batchRow = batchJobService.getBatchRowByReqId(reqID);
			// Set status accordingly based on the batch row status
			if (batchRow != null) {
				// Set the status based on the status of the batch row
				if (batchRow.getBatchStatus() == BatchStatus.BatchSuccess) {
					status = BatchStatus.BatchSuccess;
				} else if (batchRow.getBatchStatus() == BatchStatus.BatchFailed) {
					status = BatchStatus.BatchFailed;
				}

				// Update the status of the batch row in the database
				try {
					batchJobService.updateBatchRowStatus(batchRow.getRowId(), status);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else if (dbStatus == BatchStatus.BatchAborted) {
			return new SlowQueryResult(BatchStatus.BatchAborted, null, null, null, null);
		} else {
			// Insert new REDIS record
			jedissrv.setRedisStatusSlowQuery(redisKey, BatchStatus.BatchTryLater);

			// Set status to BatchTryLater
			status = BatchStatus.BatchTryLater;
		}

		return new SlowQueryResult(status, null, null, null, null); // Default return if status
																	// is not determined
	}

	public void Abort(String reqID) throws Exception {

		// Fetch the batch job associated with the reqID
		Batches batch = batchJobService.getBatchByReqId(reqID);
		if (batch == null) {
			throw new Exception("Invalid request ID");
		}

		// Check if the batch type is 'Q'
		if (batch.getType() != 'Q') {
			throw new Exception("Batch type is not 'Q'");
		}

		// Check if the batch status is not 'Aborted', 'Success', or 'Failed'
		if (batch.getStatus() == BatchStatus.BatchAborted || batch.getStatus() == BatchStatus.BatchSuccess
				|| batch.getStatus() == BatchStatus.BatchFailed) {
			throw new Exception("Cannot abort batch with status " + batch.getStatus());
		}

		// Update the batch and batch rows records
		batchJobService.abortBatchAndRows(batch);

		// Set the REDIS batch status record to 'Aborted'
		jedissrv.updateStatusInRedis(UUID.fromString(reqID), BatchStatus.BatchAborted,
				jedissrv.ALYA_BATCHSTATUS_CACHEDUR_SEC * 100);
	}

	public List<SlowQueriesResultList> list(String app, String op, int age) {
		List<SlowQueriesResultList> slowQueries = new ArrayList<>();

		// Calculate threshold time based on age
		LocalDateTime thresholdTime = LocalDateTime.now().minusDays(age);

		// Retrieve slow queries from repository
		List<Batches> batchRowsList = batchJobService.findByAppAndOpAndReqatAfter(app, op, thresholdTime);

		// Map BatchRows to SlowQueryDetails_t
		for (Batches batchs : batchRowsList) {
			SlowQueriesResultList slowQuery = new SlowQueriesResultList();
			slowQuery.setId(batchs.getId().toString());
			slowQuery.setApp(batchs.getApp());
			slowQuery.setOp(batchs.getOp());
			slowQuery.setInputfile(batchs.getInputfile()); // No input file available in BatchRows entity
			slowQuery.setStatus(batchs.getStatus());
			slowQuery.setReqat(batchs.getReqat());
			slowQuery.setDoneat(batchs.getDoneat());
			// Output files mapping not available in BatchRows entity
			slowQuery.setOutputfiles(null);

			slowQueries.add(slowQuery);
		}

		return slowQueries;
	}

}
