package com.remiges.alya.jobs;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.remiges.alya.service.BatchJobService;
import com.remiges.alya.entity.BatchJob;
import com.remiges.alya.jobs.Initializer.InitBlock;

@Component
public class JobMgr {

    private static final Logger logger = LoggerFactory.getLogger(JobMgr.class);

    private final Map<String, Initializer> initializers = new ConcurrentHashMap<>();
    private final Map<String, InitBlock> initBlocks = new ConcurrentHashMap<>();

    private final Map<String, BatchProcessor> batchProcessors = new ConcurrentHashMap<>();
    private final Map<String, SQProcessor> slowQueryProcessor = new ConcurrentHashMap<>();

    // Lock object for synchronization
    private final Object lock = new Object();

    @Autowired
    BatchJobService batchJobService;

    boolean bprocessJobs = true;

    Thread jobprocessoThread = null;

    // @Autowired
    public JobMgr() {
        // this.batchJobService = myService;

        jobprocessoThread = new Thread(new JobProcessor());

    }

    public String DoJobs() {
        if (jobprocessoThread == null) {
            return "job process thread not exists";
        }

        jobprocessoThread.start();
        return "";
    }
    public void Shutdown() {
        bprocessJobs = false;
        // jobprocessoThread.
    }

    public String processRow(BatchJob rowtoprocess) {

        if (rowtoprocess.getline() == 0) {
            return processSlowQuery(rowtoprocess);
        } else {
            return processBatch(rowtoprocess);
        }

    }

    private String processSlowQuery(BatchJob rowtoprocess) {

        String processorKey = rowtoprocess.getapp() + rowtoprocess.getop();

        SQProcessor sqProcessor = slowQueryProcessor.get(processorKey);

        if (sqProcessor == null) {
            String erlog = "No SQ Processor found for app " + rowtoprocess.getapp()
                    + " and for OP " + rowtoprocess.getop();
            logger.debug(erlog);
            return erlog;

        }

        try {
            InitBlock batchInitBlock = getOrCreateInitBlock(rowtoprocess.getapp());

            BatchOutput batchoutput = sqProcessor.DoSlowQuery(batchInitBlock, rowtoprocess.getcontext(),
                    rowtoprocess.getinput());

            if (batchoutput.error.equals(ErrorCodes.NOERROR)) {
                updateSlowQueryJobResult(rowtoprocess, batchoutput);
            } else {
                String erlog = "failed to process sq for app " + rowtoprocess.getapp();
                logger.debug(erlog);
                return erlog;
            }

            return "";

        } catch (IllegalStateException exs) {
            String erlog = "No Initializer found for app " + rowtoprocess.getapp();
            logger.debug(erlog);
            return erlog;

        }
    }

    /**
     * Update slow query processor result to database.
     * 
     * @param rowtoproces - row for which this result belong
     * @param batchOutput - result to update in DB
     * @return error string if any
     */
    private String updateSlowQueryJobResult(BatchJob rowtoproces, BatchOutput batchOutput) {
        try {
            batchJobService.updateBatchRowSlowQueryoutput(rowtoproces, batchOutput);
            return "";
        } catch (Exception ex) {
            String erlog = "failed to update result for app" + rowtoproces.getapp();
            logger.debug(erlog);
            return erlog;
        }
    }

    /**
     * update batch job result to DB.
     * 
     * @param rowtoproces
     * @param batchOutput
     */
    private void updateBatchJobResult(BatchJob rowtoproces, BatchOutput batchOutput) {

    }

    /**
     * 
     * @param rowtoprocess
     * @return
     */
    private String processBatch(BatchJob rowtoprocess) {

        String processorKey = rowtoprocess.getapp() + rowtoprocess.getop();

        BatchProcessor batchProcessor = batchProcessors.get(processorKey);

        if (batchProcessor == null) {
            String erlog = "No batch Processor found for app " + rowtoprocess.getapp()
                    + " and for OP " + rowtoprocess.getop();
            logger.debug(erlog);
            return erlog;

        }

        try {
            InitBlock batchInitBlock = getOrCreateInitBlock(rowtoprocess.getapp());

            BatchOutput batchoutput = batchProcessor.DoBatchJob(batchInitBlock, rowtoprocess.getcontext(),
                    rowtoprocess.getline(), rowtoprocess.getinput());

            if (batchoutput.error.equals(ErrorCodes.NOERROR)) {
                updateBatchJobResult(rowtoprocess, batchoutput);
            } else {
                String erlog = "failed to process batchrow for app " + rowtoprocess.getapp();
                logger.debug(erlog);
                return erlog;
            }

            return "";

        } catch (IllegalStateException exs) {
            String erlog = "No Initializer found for app " + rowtoprocess.getapp();
            logger.debug(erlog);
            return erlog;

        }

    }

    public class JobProcessor implements Runnable {

        @Override
        public void run() {

            while (bprocessJobs) {

                List<BatchJob> allQueuedBatchRow = batchJobService.getAllQueuedBatchRow(BatchStatus.BatchQueued.name());

                if (allQueuedBatchRow.isEmpty() == true) {
                    try {
                        Thread.sleep(30);
                        continue;
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                batchJobService.SwitchBatchToInprogress(allQueuedBatchRow, BatchStatus.BatchInProgress);

                allQueuedBatchRow.forEach(row -> {
                    String error = processRow(row);
                    if (error.equals("") == false)
                        logger.debug("process row failed for rowid " + row.getrowid());

                });

            }
        }

    }

    /**
     * getOrCreateInitBlock method to retrieve or create an InitBlock for the given
     * app
     * 
     * @param app - app for which to return InitBlock
     * @return
     */
    public synchronized InitBlock getOrCreateInitBlock(String app) {
        String erlog = "getOrCreateInitBlock method to retrieve or create an InitBlock for the given app...";
        logger.info(erlog);
        // Synchronize access to ensure thread safety
        synchronized (lock) {

            // Check if an InitBlock already exists for the app
            if (initBlocks.containsKey(app)) {
                return initBlocks.get(app);
            }

            // Check if an Initializer is registered for the app
            Initializer initializer = initializers.get(app);
            if (initializer == null) {
                erlog = "No initializer registered for app: " + app;
                logger.debug(erlog);
                throw new IllegalStateException(erlog);
            }

            // Create a new InitBlock using the registered Initializer
            InitBlock initBlock = initializer.init(app);

            // Cache the InitBlock for future use
            initBlocks.put(app, initBlock);
            erlog = "Create a new InitBlock using the registered Initializer...";
            logger.debug(erlog);

            return initBlock;
        }
    }


    /**
     * RegisterInitializer method to register an initializer for a specific
     * application
     * 
     * @param app         - application for which to register initializer instance.
     * @param initializer - BatchInitiazer instance to register.
     */
    //
    public synchronized void registerInitializer(String app, Initializer initializer) {
        // Check if an initializer for this app already exists
        String erlog = "";
        if (initializers.containsKey(app)) {
            erlog = "Initializer already registered for app: " + app;
            logger.debug(erlog);
            throw new IllegalStateException(erlog);
        }
        // Register the initializer for the app
        erlog = "Register the initializer for the app.......";
        logger.info(erlog);

        initializers.put(app, initializer);
        erlog = "Fetch the register initializer for the app......." + initializers.get(app);
        logger.info(erlog);
    }

    /**
     * Register Batch processor for the given app
     * 
     * @param app       - app name for which to register Batch processor
     * @param op        - operation type for which to register processor
     * @param processor - processor instance to register.
     */
    public synchronized void RegisterProcessor(String app, String op, BatchProcessor processor) {

        String key = app + op;
        // Check if an initializer for this app already exists
        String logst = "";
        if (batchProcessors.containsKey(key)) {
            logst = "BatchProcessor already registered for app: " + key;
            logger.debug(logst);
            throw new IllegalStateException(logst);
        }
        // Register the initializer for the app
        logst = "Register the BatchProcessor for the app.......";
        logger.info(logst);

        batchProcessors.put(key, processor);
        logst = "Fetch the register BatchProcessor for the app......." + batchProcessors.get(key);
        logger.info(logst);

    }

    /**
     * Register Slow Query processor for the given app
     * 
     * @param app       - app name for which to register slowQuery processor
     * @param op        - operation type for which to register processor
     * @param processor - processor instance to register.
     */
    public synchronized void RegisterSQProcessor(String app, String op, SQProcessor processor) {

        String key = app + op;
        // Check if an initializer for this app already exists
        if (slowQueryProcessor.containsKey(key)) {
            String erlog = "SQProcessor already registered for app: " + key;
            logger.debug(erlog);
            throw new IllegalStateException(erlog);
        }
        // Register the initializer for the app
        String erlog = "Register the SQProcessor for the app.......";
        logger.info(erlog);

        slowQueryProcessor.put(key, processor);
        erlog = "Fetch the register SQProcessor for the app......." + slowQueryProcessor.get(key);
        logger.info(erlog);
    }

}
