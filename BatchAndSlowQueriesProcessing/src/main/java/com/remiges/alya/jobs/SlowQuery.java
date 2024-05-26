package com.remiges.alya.jobs;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.config.JobManagerConfig;
import com.remiges.alya.service.BatchJobService;
import com.remiges.alya.service.JedisService;

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

}
