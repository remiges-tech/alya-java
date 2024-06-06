package com.remiges.alya.jobs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

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
	private final Logger logger = Logger.getLogger(SlowQuery.class.getName());;

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
	 * Alya.SlowQuery.Submit() : Submits a slow query to the batch processing
	 * framework.
	 * --------------------------------------------------------------------------
	 * This method is used to submit a slow query to Alya's batch processing
	 * framework. It generates a unique request ID, writes a record in the batches
	 * table with type set to Q, and a record in batchrows. The input parameters
	 * app, op, context, and input are mandatory.
	 * --------------------------------------------------------------------------
	 * Parameters: - app: One-word name of the application. - op: Name or label of
	 * the operation. - context: JSON version of context data containing
	 * authorization credentials, IP address, etc. - input: JSON version of fields
	 * received by the WSC for processing.
	 * --------------------------------------------------------------------------
	 * Response: - reqID: Unique ID of the queued request if successful. - err:
	 * Error object carrying various errors including input parameter errors,
	 * component failure errors.
	 * --------------------------------------------------------------------------
	 * Note: The input parameter can include a large input file for processing,
	 * which can be stored in an object store or shared file system. Alya does not
	 * manage the input file's storage; it is the responsibility of the caller.
	 * --------------------------------------------------------------------------
	 * Throws: - IllegalArgumentException: If any of the parameters are null or
	 * empty. - ProcessingException: If there is a failure during processing, such
	 * as database access failure.
	 */

	public String submit(String app, String op, JsonNode context, String input) {
		try {
			UUID reqID = batchJobService.saveSlowQueries(app, op, context, input, BatchStatus.BatchQueued);
			logger.info("Submitted job with request ID: " + reqID);
			return reqID != null ? reqID.toString() : null;
		} catch (Exception e) {
			logger.severe("Error occurred while submitting the job: " + e.getMessage());
			e.printStackTrace();
			return "An error occurred while submitting the job: " + e.getMessage();
		}
	}

	/**
	 * Alya.SlowQuery.Done() : Checks if a slow query is complete or not.
	 * *************************************************************************
	 * This method is used to poll Alya's batch processing framework to determine
	 * the status and result of a slow query. It checks REDIS for the status of the
	 * slow query request identified by reqID. If the status is available in REDIS,
	 * it returns the status and any relevant data. If the status is not available
	 * in REDIS, it queries the database for the status and returns the result
	 * accordingly. The caller should invoke this method at intervals of several
	 * seconds.
	 * *************************************************************************
	 * Parameters: - reqID: Unique ID of the slow query request.
	 * *************************************************************************
	 * Returns: - status: Status of the slow query, including BatchTryLater,
	 * BatchSuccess, BatchFailed, BatchAborted, etc. - result: JSON data structure
	 * containing the result of the processing, unmarshalled by the caller. -
	 * messages: Array of error messages in Alya.ErrorMessage format, if any errors
	 * occurred during processing. - outputfiles: Map of file paths or object IDs
	 * for any files generated by the processing function. - err: Error status as
	 * per the Go convention, indicating critical system errors like database access
	 * errors.
	 * ***************************************************************************
	 * Note: - The status BatchTryLater indicates that the query is still in
	 * progress and should be polled again later. - The output parameters result,
	 * messages, and outputfiles carry useful data based on the status of the query.
	 * - If status is BatchSuccess, the caller should unmarshall the result object
	 * to extract the processing result. - If status is not BatchTryLater or
	 * BatchAborted, the caller should process the response according to the
	 * specified sequence. - The slow query may generate files, and their paths or
	 * object IDs may be included in the result.
	 * ****************************************************************************
	 * Throws: - IllegalArgumentException: If the reqID parameter is null or empty.
	 * - ProcessingException: If there is a failure during processing, such as
	 * database access failure.
	 */
	public SlowQueryResult done(String reqID) {
		String redisKey = "ALYA_BATCHSTATUS_" + reqID;
		BatchStatus status = BatchStatus.BatchTryLater;

		try {
			String redisValue = jedissrv.getBatchStatusFromRedis(redisKey);
			Boolean foundInCache = false;
			if (redisValue != null) {
				status = BatchStatus.valueOf(redisValue);
				foundInCache = true;
			} else {
				status = batchJobService.getSlowQueryStatusByReqId(reqID);
			}

			if (status == BatchStatus.BatchSuccess || status == BatchStatus.BatchFailed) {
				BatchRows batchRow = batchJobService.getSQBatchRowByReqId(reqID);
				if (batchRow != null) {
					status = batchRow.getBatchStatus();

					// if Status not found in cache and status is failed, abort, success
					// then set status in redis
					if (!foundInCache)
						jedissrv.updateStatusInRedis(UUID.fromString(reqID), status);

					return new SlowQueryResult(status, batchRow.getRes(), batchRow.getMessages(),
							batchRow.getBlobrows(), null);
				} else {
					throw new Exception(
							"Invalid request ID: No entry available in batch rows for request ID: " + reqID);
				}
			} else if (status == BatchStatus.BatchAborted) {
				return new SlowQueryResult(BatchStatus.BatchAborted, null, null, null, null);
			} else {

				status = BatchStatus.BatchTryLater;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new SlowQueryResult(null, null, null, null, e);
		}

		return new SlowQueryResult(status, null, null, null, null);
	}

	/**
	 * Alya.SlowQuery.Abort() : Aborts a slow query job.
	 ******************************************************************************
	 * This method is used to abort a slow query job that has been submitted to
	 * Alya's batch processing framework. It updates the status of the job to
	 * "aborted" in both the batches and batchrows tables, provided that the job
	 * status is not already "aborted", "success", or "failed". The method starts by
	 * initiating a database transaction and then selects records for update across
	 * both tables based on the request ID. It inspects the status and doneat fields
	 * of the records and updates them if necessary, setting the status to "aborted"
	 * and doneat to the current timestamp. The REDIS batch status record for this
	 * job is also updated to "aborted" if the database records have been
	 * successfully updated.
	 ******************************************************************************
	 * Parameters: - reqID: Unique ID of the slow query job to be aborted.
	 ******************************************************************************
	 * Returns: - err: Error object indicating any system failures or invalid
	 * request ID. If successful, err will be nil.
	 ******************************************************************************
	 * Throws: - IllegalArgumentException: If the reqID parameter is null or empty.
	 * - DatabaseOperationException: If there is a failure during database
	 * operation, such as transaction failure or record update failure.
	 */

	public void abort(String reqID) throws Exception {
		try {
			Batches batch = batchJobService.getBatchByReqId(reqID);
			if (batch == null) {
				throw new Exception("Invalid request ID");
			}

			char type = batch.getType();
			BatchStatus status = batch.getStatus();
			if (type != AlyaConstant.TYPE_Q) {
				throw new Exception("Batch type is not 'Q'"); // need to impl in done also
			}
			if (status == BatchStatus.BatchAborted || status == BatchStatus.BatchSuccess
					|| status == BatchStatus.BatchFailed) {
				throw new Exception("Cannot abort batch with status " + status);
			}

			batchJobService.abortBatchAndRows(batch);
			jedissrv.updateStatusInRedis(UUID.fromString(reqID), BatchStatus.BatchAborted);
			logger.info("Batch with request ID " + reqID + " aborted successfully.");
		} catch (Exception e) {
			logger.severe("Error occurred while aborting the batch: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Alya.SlowQuery.List() : Lists all the slow queries currently being processed
	 * or processed in the past against a specific app.
	 * ****************************************************************************
	 * This method retrieves details of slow queries from the batches table for a
	 * specified app and optionally a specific operation (op). The age parameter
	 * specifies the age of the records to return, checking backward in time from
	 * the current time. Only records with a reqat timestamp less than the specified
	 * age will be included in the result.
	 * *****************************************************************************
	 * Parameters: - app: The application name for which the slow queries will be
	 * returned. - op: (Optional) The operation name. If provided, only slow queries
	 * with matching operation names will be returned. - age: The age of the records
	 * to return, specified in days. Must be greater than 0.
	 * *****************************************************************************
	 * Returns: - sqlist: An array containing details of the slow queries. Each
	 * entry in the array represents a single slow query and includes fields such as
	 * ID, app, op, inputfile, status, reqat, doneat, and outputfiles. - err: An
	 * error object indicating any system errors, such as a database access error.
	 * If successful, err will be nil.
	 * ****************************************************************************
	 * Throws: - IllegalArgumentException: If the app parameter is null or empty, or
	 * if the age parameter is less than or equal to 0. - DatabaseAccessException:
	 * If there is an error accessing the database.
	 */

	public List<SlowQueriesResultList> list(String app, String op, int age) {
		List<SlowQueriesResultList> slowQueries = new ArrayList<>();

		// Calculate threshold time based on age
		LocalDateTime thresholdTime = LocalDateTime.now().minusDays(age);

		logger.info("Retrieving slow queries from repository for app: " + app + ", op: " + op + ", and age: " + age);

		// Retrieve slow queries from repository
		List<Batches> batchRowsList = batchJobService.findBatchesByAppAndOpAndReqAtAfter(app, op, thresholdTime);
		logger.info("Retrieved " + batchRowsList.size() + " batches from the repository." + thresholdTime);

		batchRowsList.forEach(batch -> {
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
		});

		logger.info("Finished processing slow queries.");
		return slowQueries;
	}
}
