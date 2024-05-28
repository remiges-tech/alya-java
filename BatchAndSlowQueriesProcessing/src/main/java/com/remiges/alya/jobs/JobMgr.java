package com.remiges.alya.jobs;

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

import com.remiges.alya.service.BatchJobService;
import com.remiges.alya.service.JedisService;
import com.remiges.alya.config.JobManagerConfig;
import com.remiges.alya.entity.BatchJob;
import com.remiges.alya.jobs.Initializer.InitBlock;


@Component
@Scope("prototype")
public class JobMgr {

    private static final Logger logger = LoggerFactory.getLogger(JobMgr.class);

    private final Map<String, Initializer> initializers = new ConcurrentHashMap<>();
    private final Map<String, InitBlock> initBlocks = new ConcurrentHashMap<>();
    private final Map<String, BatchProcessor> batchProcessors = new ConcurrentHashMap<>();
    private final Map<String, SQProcessor> slowQueryProcessor = new ConcurrentHashMap<>();
    private final Object lock = new Object();
    private BatchJobService batchJobService;
    private boolean bprocessJobs = true;
    private Thread jobprocessoThread = null;

    private JedisService jedissrv;

    JobManagerConfig mgrConfig;

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
    public synchronized String processRow(BatchJob rowtoprocess) {
        if (rowtoprocess.getLine() == 0) {
            return processSlowQuery(rowtoprocess);
        } else {
            return processBatch(rowtoprocess);
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

        if (sqProcessor == null) {
            String erlog = "No SQ Processor found for app " + rowtoprocess.getApp()
                    + " and for OP " + rowtoprocess.getOp();
            logger.debug(erlog);
            return erlog;
        }

        try {
            InitBlock batchInitBlock = getOrCreateInitBlock(rowtoprocess.getApp());

            BatchOutput batchoutput = sqProcessor.DoSlowQuery(batchInitBlock, rowtoprocess.getContext(),
                    rowtoprocess.getInput());

            updateSlowQueryJobResult(rowtoprocess, batchoutput);

            if (batchoutput.error.equals(ErrorCodes.NOERROR)) {
                String erlog = "Success to process row for app " + rowtoprocess.getApp();
                logger.debug(erlog);

            } else {
                String erlog = "failed to process sq for app " + rowtoprocess.getApp();
                logger.debug(erlog);
                return erlog;
            }

            return "";
        } catch (IllegalStateException exs) {
            String erlog = "No Initializer found for app " + rowtoprocess.getApp();
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
            return "";
        } catch (Exception ex) {
            String erlog = "failed to update result for app " + rowtoproces.getApp();
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
            batchJobService.updateBatchRowForBatchOutput(rowtoproces, batchOutput);
            return "";
        } catch (Exception ex) {
            String erlog = "failed to update blobrow for app " + rowtoproces.getApp() + ex.getMessage();
            logger.debug(erlog);
            return erlog;
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
            String erlog = "No batch Processor found for app " + rowtoprocess.getApp()
                    + " and for OP " + rowtoprocess.getOp();
            logger.debug(erlog);
            return erlog;
        }

        try {
            InitBlock batchInitBlock = getOrCreateInitBlock(rowtoprocess.getApp());

            BatchOutput batchoutput = batchProcessor.DoBatchJob(batchInitBlock, rowtoprocess.getContext(),
                    rowtoprocess.getLine(), rowtoprocess.getInput());
            updateBatchJobResult(rowtoprocess, batchoutput);
            if (batchoutput.error.equals(ErrorCodes.NOERROR)) {
                String erlog = "Sucess to process batchrow for app " + rowtoprocess.getApp();
                logger.debug(erlog);

            } else {
                String erlog = "failed to process batchrow for app " + rowtoprocess.getApp();
                logger.debug(erlog);
                return erlog;
            }

            return "";
        } catch (IllegalStateException exs) {
            String erlog = "No Initializer found for app " + rowtoprocess.getApp();
            logger.debug(erlog);
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
                List<BatchJob> allQueuedBatchRow = batchJobService.getAllQueuedBatchRows(BatchStatus.BatchQueued);

                allQueuedBatchRow.forEach(bat -> {
                    // System.out.println(bat.getbatch().toString());
                    System.out.println(bat.getInput().toString());
                });

                // batch is empty go to sleep
                if (allQueuedBatchRow.isEmpty()) {
                    try {
                        Thread.sleep(getRandomSleepDuration());
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Set<UUID> uniqueBatchIds = allQueuedBatchRow.stream()
                        .map(batch -> batch.getId())
                        .collect(Collectors.toSet());

                try {
                    batchJobService.SwitchBatchToInprogress(allQueuedBatchRow, uniqueBatchIds,
                            BatchStatus.BatchInProgress);

                    // collect batchId to summarize
                    Set<UUID> BatchIdToSummarize = new HashSet<>();

                    allQueuedBatchRow.forEach(row -> {
                        String error = processRow(row);
                        if (!error.isEmpty())
                            logger.debug("process row failed for rowid " + row.getRowId());
                        else
                            BatchIdToSummarize.add(row.getId());

                    });

                    for (UUID batchId : BatchIdToSummarize) {
                        batchJobService.SummarizeBatch(batchId);
                    }

                } catch (Exception ex) {
                    // Log exception if needed
                    logger.debug("Exception caught {}", ex.toString());
                }
            }
        }
    }

    /**
     * Generates a random sleep duration between 30 and 60 seconds.
     * 
     * @return the random sleep duration in milliseconds
     */
    public static long getRandomSleepDuration() {
        Random rand = new Random();
        int randomSeconds = rand.nextInt(31) + 30; // Random number between 30 and 60 (inclusive)
        return randomSeconds * 1000; // Convert seconds to milliseconds
    }

    /**
     * Retrieve or create an InitBlock for the given app.
     * 
     * @param app the app for which to return InitBlock
     * @return the InitBlock for the given app
     */
    public synchronized InitBlock getOrCreateInitBlock(String app) {
        logger.info("getOrCreateInitBlock method to retrieve or create an InitBlock for the given app...");
        synchronized (lock) {
            if (initBlocks.containsKey(app)) {
                return initBlocks.get(app);
            }

            Initializer initializer = initializers.get(app);
            if (initializer == null) {
                String erlog = "No initializer registered for app: " + app;
                logger.debug(erlog);
                throw new IllegalStateException(erlog);
            }

            InitBlock initBlock = initializer.init(app);
            initBlocks.put(app, initBlock);
            logger.debug("Create a new InitBlock using the registered Initializer...");

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
            logger.debug(erlog);
            throw new IllegalStateException(erlog);
        }
        logger.info("Register the initializer for the app.......");
        initializers.put(app, initializer);
        logger.info("Fetch the registered initializer for the app......." + initializers.get(app));
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
            throw new IllegalStateException(logst);
        }
        logger.info("Register the BatchProcessor for the app.......");
        batchProcessors.put(key, processor);
        logger.info("Fetch the registered BatchProcessor for the app......." + batchProcessors.get(key));
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
            throw new IllegalStateException(erlog);
        }
        logger.info("Register the SQProcessor for the app.......");
        slowQueryProcessor.put(key, processor);
        logger.info("Fetch the registered SQProcessor for the app......." + slowQueryProcessor.get(key));
    }
}
