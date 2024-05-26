package com.remiges.alya.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.config.JobManagerConfig;
import com.remiges.alya.entity.BatchRows;
import com.remiges.alya.jobs.SlowQueryResult.AlyaErrorMessage;
import com.remiges.alya.service.BatchJobService;
import com.remiges.alya.service.JedisService;;

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
		// Generate a unique request ID
		UUID reqID = null;

		try {

			reqID = batchJobService.SaveSlowQueries(app, op, context, input, BatchStatus.BatchQueued);

		} catch (Exception e) {
			// Handle any exceptions and return error
			return e.getMessage();
		}

		// Return the unique request ID
		return reqID.toString();
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

}
