package com.remiges.alya.jobs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.remiges.alya.config.JobManagerConfig;
import com.remiges.alya.entity.BatchJob;
import com.remiges.alya.service.BatchJobService;
import com.remiges.alya.service.JedisService;
import com.remiges.alya.service.MinioService;

@Component
@Scope("prototype")
public class JobMgr {

	// ApplicationContext applicationContext = ApplicationContextProviderAlya.getApplicationContext();
    // LogharbourUtilityAlya logharbourUtility = applicationContext.getBean(LogharbourUtilityAlya.class);
	private static final Logger logger = LoggerFactory.getLogger(JobMgr.class);

	private final Map<String, Initializer> initializers = new ConcurrentHashMap<>();
	private final Map<String, BatchInitBlocks> initBlocks = new ConcurrentHashMap<>();
	private final Map<String, BatchProcessor> batchProcessors = new ConcurrentHashMap<>();
	private final Map<String, SQProcessor> slowQueryProcessor = new ConcurrentHashMap<>();
	private static final Object lock = new Object();
	private static final Object lock2 = new Object();
	private BatchJobService batchJobService;
	private boolean bprocessJobs = true;
	private Thread jobprocessoThread = null;

	private JedisService jedissrv;

	JobManagerConfig mgrConfig;

	@Autowired
	private MinioService minioService;

	/**
	 * Constructor for JobMgr.
	 * 
	 * @param batchJobService the service used for batch job operations
	 */
	@Autowired
	public JobMgr(BatchJobService batchJobService, JedisService jedissrv, JobManagerConfig mgrconfig) {
		this.batchJobService = batchJobService;
		this.jedissrv = jedissrv;
		this.mgrConfig = mgrconfig;
		jobprocessoThread = new Thread(new JobProcessor());

	}
	/**
	 * Starts the job processing thread.
	 * 
	 * @return a message indicating the status of the job processing thread
	 */
	public String DoJobs() {
		if (jobprocessoThread == null) {
			// logharbourUtility.collectActivityLogs("job process thread not exists",AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
			logger.error("ALYA : JobMgr : DoJobs : "+"job process thread not exists");
			return "job process thread not exists";
		}
		jobprocessoThread.start();
		return "";
	}

	/**
	 * Shuts down the job processing.
	 */
	public void Shutdown() {
		bprocessJobs = false;
	}

	/**
	 * Processes a row in a batch job.
	 * 
	 * @param rowtoprocess the batch job row to process
	 * @return a message indicating the result of the processing
	 */
	public String processRow(BatchJob rowtoprocess) {
		if (rowtoprocess.getLine() == 0) {
			return processSlowQuery(rowtoprocess);
		} else {
			return processBatch(rowtoprocess);
		}
	}

	/**
	 * Retrieve the Batch Processor and InitBlock to process the Batch by calling
	 * DoBatchJob and Update Batch results.
	 * 
	 * @param rowtoprocess the batch row to process
	 * @return a message indicating the result of the processing
	 */
	private String processBatch(BatchJob rowtoprocess) {
		String processorKey = rowtoprocess.getApp() + rowtoprocess.getOp();
		BatchProcessor batchProcessor = batchProcessors.get(processorKey);

		if (batchProcessor == null) {
			String erlog = "No batch Processor found for app " + rowtoprocess.getApp() + " and for OP "
					+ rowtoprocess.getOp();
					// logharbourUtility.collectActivityLogs(erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");

		    logger.error("ALYA : JobMgr : processBatch : " + erlog);
			return erlog;
		}

		try {
			BatchInitBlocks batchInitBlock = getOrCreateInitBlock(rowtoprocess.getApp());

			BatchOutput batchoutput = batchProcessor.DoBatchJob(batchInitBlock, rowtoprocess.getContext(),
					rowtoprocess.getLine(), rowtoprocess.getInput());
			updateBatchJobResult(rowtoprocess, batchoutput);
			if (batchoutput.error.equals(ErrorCodes.NOERROR)) {

			} else {
				String erlog = "failed to process batchrow for app " + rowtoprocess.getApp();
				// logharbourUtility.collectActivityLogs(erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
				logger.error("ALYA : JobMgr : processBatch : " + erlog);

				// logger.debug(erlog);
				return erlog;
			}

			return "";
		} catch (IllegalStateException exs) {
			String erlog = "No Initializer found for app " + rowtoprocess.getApp();
			// logharbourUtility.collectActivityLogs(erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
			logger.error("ALYA : JobMgr : processBatch : " + erlog);

			// logger.debug(erlog);
			return erlog;
		}
	}

	/**
	 * JobProcessor is a Runnable class that processes jobs in a separate thread.
	 */
	public class JobProcessor implements Runnable {

		@Override
		public void run() {
			while (bprocessJobs) {
				// logharbourUtility.collectActivityLogs("JobProcessor : run : 1 : Initiat Request",AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");

				List<BatchJob> allQueuedBatchRow = new ArrayList<>();
				synchronized (lock) {
					// logharbourUtility.collectActivityLogs("JobProcessor : run : 2 : synchronized Block 1",AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
					allQueuedBatchRow = batchJobService.getAllQueuedBatchRows(BatchStatus.BatchQueued);
					logger.error("ALYA : JobMgr : run : " + "allQueuedBatchRow "+allQueuedBatchRow.size());
					// allQueuedBatchRow.forEach(bat -> {
					// 	logger.error("ALYA : JobMgr : run : " + bat.getLine().toString());
					// });

				}
				// batch is empty go to sleep
				if (allQueuedBatchRow.isEmpty()) {
					// logharbourUtility.collectActivityLogs("JobProcessor : run : 3 : synchronized Block",AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");

					try {
						Thread.sleep(getRandomSleepDuration());
						logger.error("ALYA : JobMgr : run : " + "allQueuedBatchRow is Empty so calling getRandomSleepDuration");

						continue;
					} catch (Exception e) {
						// logharbourUtility.collectActivityLogs("JobProcessor : run : ERROR : All queued Batch row is empty.",AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
						logger.error("ALYA : JobMgr : run : " + e.getMessage());

						e.printStackTrace();
					}
				}

				try {

					synchronized (lock) {
						logger.info("JobProcessor : run : 4 : synchronized Block 2");
						// logharbourUtility.collectActivityLogs("JobProcessor : run : 4 : synchronized Block 2",AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");

						Set<UUID> uniqueBatchIds = allQueuedBatchRow.stream().map(batch -> batch.getId())
								.collect(Collectors.toSet());
								logger.error("ALYA : JobMgr : JobProcessor : " + "uniqueBatchIds for allQueuedBatchRow -> "+  uniqueBatchIds.size());

								// logharbourUtility.collectActivityLogs("JobProcessor : run : 4 : synchronized Block 2 -> "+ allQueuedBatchRow.stream().map(batch -> batch.getId())
								// .collect(Collectors.toSet()),AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");


						batchJobService.SwitchBatchToInprogress(allQueuedBatchRow, uniqueBatchIds,
								BatchStatus.BatchInProgress);
								logger.error("ALYA : JobMgr : JobProcessor : "+ " calling SwitchBatchToInprogress for uniqueBatchIds -> "+uniqueBatchIds.size() );
								// logharbourUtility.collectActivityLogs("JobProcessor : run : 5 : synchronized Block 2 -> "+ BatchStatus.BatchInProgress,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
					}

					// collect batchId to summarize
					Set<UUID> BatchIdToSummarize = new HashSet<>();

					allQueuedBatchRow.forEach(row -> {
						String error = processRow(row);
						if (!error.isEmpty()){
							logger.error("ALYA : JobMgr : JobProcessor : "+"Empty row  for rowid -> " + row.getRowId());
						}
						// logharbourUtility.collectActivityLogs("JobProcessor : run : ERROR :Empty row  for rowid -> " + row.getRowId(),AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
						else
							{
								BatchIdToSummarize.add(row.getId());
								logger.error("ALYA : JobMgr : JobProcessor : "+"Row added for summarization for rowid -> " + row.getRowId());

								// logharbourUtility.collectActivityLogs("JobProcessor : run : 6 : Row added to Batch summarization -> "+ row.getId(),AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
						}


					});

				
					synchronized (lock2) {

						for (UUID batchId : BatchIdToSummarize) {
							// logharbourUtility.collectActivityLogs("JobProcessor : run : 7 : Summarized Batch for BatchID -> "+batchId,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
							batchJobService.SummarizeBatch(batchId);
							logger.error("ALYA : JobMgr : JobProcessor : "+"Calling SummarizeBatch for batchId" + batchId);
							

						}

					}

				} catch (Exception ex) {
					logger.error("ALYA : JobMgr : JobProcessor : "+"Error -> " + ex.toString());

					// logharbourUtility.collectActivityLogs("JobProcessor : run : ERROR : ERROR -> " + ex.toString(),AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
				}

			}
		}
	}

	/**
	 * Processes a slow query row in a batch job.
	 * 
	 * @param rowtoprocess the batch job row to process
	 * @return a message indicating the result of the processing
	 */
	private String processSlowQuery(BatchJob rowtoprocess) {
		String processorKey = rowtoprocess.getApp() + rowtoprocess.getOp();
		SQProcessor sqProcessor = slowQueryProcessor.get(processorKey);
		// logharbourUtility.collectActivityLogs("JobProcessor : processSlowQuery : 1 : sqProcessor -> "+ sqProcessor.toString(),AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");

		if (sqProcessor == null) {
			String erlog = "No SQ Processor found for app " + rowtoprocess.getApp()
					+ " and for OP " + rowtoprocess.getOp();
			logger.debug(erlog);
			logger.error("ALYA : JobMgr : processSlowQuery : "+ erlog);

			// logharbourUtility.collectActivityLogs("ERROR "+ erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");

			return erlog;
		}

		try {
			BatchInitBlocks batchInitBlock = getOrCreateInitBlock(rowtoprocess.getApp());

			BatchOutput batchoutput = sqProcessor.DoSlowQuery(batchInitBlock, rowtoprocess.getContext(),
					rowtoprocess.getInput());
					// logharbourUtility.collectActivityLogs("JobProcessor : processSlowQuery : 2 : BatchOutPut -> "+ batchoutput.toString(),AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
					logger.error("ALYA : JobMgr : processSlowQuery : "+"updateSlowQueryJobResult for BatchOutPut -> "+ batchoutput.toString());
					


			updateSlowQueryJobResult(rowtoprocess, batchoutput);

			if (batchoutput.error.equals(ErrorCodes.NOERROR)) {
				String erlog = "Success to process row for app " + rowtoprocess.getApp();
				logger.error("ALYA : JobMgr : processSlowQuery : "+erlog);
				// logharbourUtility.collectActivityLogs("JobProcessor : processSlowQuery : 3 : "+ erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");

			} else {
				String erlog = "failed to process sq for app " + rowtoprocess.getApp();
				logger.debug(erlog);
				logger.error("ALYA : JobMgr : processSlowQuery : "+erlog);

				// logharbourUtility.collectActivityLogs("JobProcessor : processSlowQuery : 4 : "+ erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");


				return erlog;
			}

			return "";
		} catch (IllegalStateException exs) {

			String erlog = "No Initializer found for app " + rowtoprocess.getApp();
			// logharbourUtility.collectActivityLogs("JobProcessor : processSlowQuery : ERROR -> "+ erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
			logger.error("ALYA : JobMgr : processSlowQuery : "+erlog);

			logger.debug(erlog);
			return erlog;
		}
	}

	/**
	 * Update slow query processor result to database.
	 * 
	 * @param rowtoproces the row for which this result belongs
	 * @param batchOutput the result to update in DB
	 * @return an error string if any
	 */
	private String updateSlowQueryJobResult(BatchJob rowtoproces, BatchOutput batchOutput) {
		try {
			batchJobService.updateBatchRowForSlowQueryoutput(rowtoproces, batchOutput);
			// logharbourUtility.collectActivityLogs("JobProcessor : updateSlowQueryJobResult : 1 : Process initiated for Row -> "+ rowtoproces.toString(),AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
			logger.error("ALYA : JobMgr : updateSlowQueryJobResult : "+"updateBatchRowForSlowQueryoutput for BatchOutPut -> "+ batchOutput.toString()+ " and rowtoprocess -> "+rowtoproces.toString() );

			return "";
		} catch (Exception ex) {
			String erlog = "failed to update result for app " + rowtoproces.getApp();
			// logharbourUtility.collectActivityLogs("JobProcessor : updateSlowQueryJobResult : ERROR -> "+ erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
			logger.error("ALYA : JobMgr : updateSlowQueryJobResult : "+"Error -> "+erlog);

			logger.debug(erlog);
			return erlog;
		}
	}

	/**
	 * Update batch job result to DB.
	 * 
	 * @param rowtoproces the row for which this result belongs
	 * @param batchOutput the result to update in DB
	 * @return an error string if any
	 */
	private String updateBatchJobResult(BatchJob rowtoproces, BatchOutput batchOutput) {
		try {
			// logharbourUtility.collectActivityLogs("JobProcessor : updateBatchJobResult : 1 : Process initiated for Row -> "+ rowtoproces.toString(),AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
			logger.error("ALYA : JobMgr : updateBatchJobResult : "+"updateBatchRowForBatchOutput initiated for BatchOutPut -> "+ batchOutput.toString()+ " and rowtoprocess -> "+rowtoproces.toString());
			batchJobService.updateBatchRowForBatchOutput(rowtoproces, batchOutput);
			return "";
		} catch (Exception ex) {

			String erlog = "failed to update blobrow for app " + rowtoproces.getApp() + ex.getMessage();
			// logharbourUtility.collectActivityLogs("JobProcessor : updateBatchJobResult : ERROR -> "+ erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
			logger.error("ALYA : JobMgr : updateBatchJobResult : "+"Error -> "+erlog);

			logger.debug(erlog);
			return erlog;
		}
	}

	/**
	 * Generates a random sleep duration between 30 and 60 seconds.
	 * 
	 * @return the random sleep duration in milliseconds
	 */
	public  long getRandomSleepDuration() {
		Random rand = new Random();
		int randomSeconds = 30; // Random number between 30 and 60 (inclusive)
		// logharbourUtility.collectActivityLogs("JobProcessor : getRandomSleepDuration : 1 : Process initiated with 30 seconds",AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
		logger.error("ALYA : JobMgr : getRandomSleepDuration : "+"Process initiated with 30 seconds");

		return randomSeconds * 1000; // Convert seconds to milliseconds
	}

	/**
	 * Retrieve or create an InitBlock for the given app.
	 * 
	 * @param app the app for which to return InitBlock
	 * @return the InitBlock for the given app
	 */
	public synchronized BatchInitBlocks getOrCreateInitBlock(String app) {
		synchronized (lock) {
			if (initBlocks.containsKey(app)) {
				// logharbourUtility.collectActivityLogs("JobProcessor : getOrCreateInitBlock : 1 : Returned InitBlock for app -> "+app,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
				logger.error("ALYA : JobMgr : getOrCreateInitBlock : "+"Returned InitBlock for app -> "+app);
				return initBlocks.get(app);
			}

			Initializer initializer = initializers.get(app);
			if (initializer == null) {
				String erlog = "No initializer registered for app: " + app;
				logger.debug(erlog);
				logger.error("ALYA : JobMgr : getOrCreateInitBlock : "+"ERROR -> "+erlog);

				// logharbourUtility.collectActivityLogs("JobProcessor : getOrCreateInitBlock : ERROR -> "+erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");


				throw new IllegalStateException(erlog);
			}

			BatchInitBlocks initBlock = initializer.init(app);
			initBlocks.put(app, initBlock);
			// logharbourUtility.collectActivityLogs("JobProcessor : getOrCreateInitBlock : 2 : InitBlock  -> "+initBlock,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
			logger.error("ALYA : JobMgr : getOrCreateInitBlock : "+"InitBlock  -> "+initBlock);

			return initBlock;
		}
	}

	/**
	 * Registers an initializer for a specific application.
	 *
	 * 
	 * 
	 * @param app         the application for which to register the initializer
	 *                    instance
	 * @param initializer the Initializer instance to register
	 */
	public synchronized void registerInitializer(String app, Initializer initializer) {
		if (initializers.containsKey(app)) {
			String erlog = "Initializer already registered for app: " + app;
			// logharbourUtility.collectActivityLogs("JobProcessor : registerInitializer : 1 : "+erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
			logger.error("ALYA : JobMgr : registerInitializer : "+"Error  -> "+erlog);

			logger.debug(erlog);
			throw new IllegalStateException(erlog);
		}
		initializers.put(app, initializer);
		// logharbourUtility.collectActivityLogs("JobProcessor : registerInitializer : 2 : Fetch the registered initializer for the app......." + initializers.get(app),AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");

	}

	/**
	 * Registers a Batch processor for the given app.
	 * 
	 * @param app       the app name for which to register the Batch processor
	 * @param op        the operation type for which to register the processor
	 * @param processor the processor instance to register
	 */
	public synchronized void RegisterProcessor(String app, String op, BatchProcessor processor) {
		String key = app + op;
		if (batchProcessors.containsKey(key)) {
			String logst = "BatchProcessor already registered for app: " + key;
			logger.debug(logst);
			logger.error("ALYA : JobMgr : RegisterProcessor : "+"ERROR : " + logst);
			// logharbourUtility.collectActivityLogs("JobProcessor : RegisterProcessor : ERROR : " + logst,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
			throw new IllegalStateException(logst);
		}
		logger.info("JobProcessor : RegisterProcessor : 1 : Register the BatchProcessor for the app.......");
		batchProcessors.put(key, processor);
		logger.error("ALYA : JobMgr : RegisterProcessor : "+"put in batchProcessor where key -> " + key +" and processor -> "+ processor.toString());

		logger.info("JobProcessor : RegisterProcessor : 2 : Fetch the registered BatchProcessor for the app......." + batchProcessors.get(key));
	}

	/**
	 * Registers a Slow Query processor for the given app.
	 * 
	 * @param app       the app name for which to register the slowQuery processor
	 * @param op        the operation type for which to register the processor
	 * @param processor the processor instance to register
	 */
	public synchronized void RegisterSQProcessor(String app, String op, SQProcessor processor) {
		String key = app + op;
		if (slowQueryProcessor.containsKey(key)) {
			String erlog = "SQProcessor already registered for app: " + key;
			logger.debug(erlog);
			// logharbourUtility.collectActivityLogs("JobProcessor : RegisterSQProcessor : ERROR -> " + erlog,AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");
			logger.error("ALYA : JobMgr : RegisterSQProcessor : "+"ERROR -> " + erlog);
			throw new IllegalStateException(erlog);
		}
		// logharbourUtility.collectActivityLogs("JobProcessor : RegisterSQProcessor : 2 : Fetch the registered SQProcessor for the app......." + slowQueryProcessor.get(key),AlyaConstant.LogharbourUtilityConstant.ALYA_MODULE_NAME,"JOB_MGR");

		slowQueryProcessor.put(key, processor);
		logger.error("ALYA : JobMgr : RegisterSQProcessor : "+"put in slowQueryProcessor where key -> " + key +" and processor -> "+ processor.toString());

	}
}
