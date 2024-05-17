package com.remiges.alya.jobs;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.remiges.alya.service.BatchJobService;
import com.remiges.alya.entity.BatchJob;

public class JobMgr {

    private final Map<String, BatchInitializer> initializers = new ConcurrentHashMap<>();
    private final Map<String, BatchInitBlock> initBlocks = new ConcurrentHashMap<>();

    private final Map<String, BatchProcessor> batchProcessors = new ConcurrentHashMap<>();
    private final Map<String, SQProcessor> slowQueryProcessor = new ConcurrentHashMap<>();

    // Lock object for synchronization
    private final Object lock = new Object();

    BatchJobService batchJobService;

    boolean bprocessJobs = true;

    Thread jobprocessoThread = null;

    public JobMgr() {

        jobprocessoThread = new Thread(new JobProcessor());
        jobprocessoThread.start();
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
            return "No SQ Processor found for app " + rowtoprocess.getapp()
                    + " and for OP " + rowtoprocess.getop();

        }

        try {
            BatchInitBlock batchInitBlock = getOrCreateInitBlock(rowtoprocess.getapp());

            BatchOutput batchoutput = sqProcessor.DoSlowQuery(batchInitBlock, rowtoprocess.getcontext(),
                    rowtoprocess.getinput());

            if (batchoutput.error.equals(ErrorCodes.NOERROR)) {
                updateSlowQueryJobResult(rowtoprocess, batchoutput);
            } else {
                return "failed to process sq for app " + rowtoprocess.getapp();
            }

            return "";

        } catch (IllegalStateException exs) {
            return "No Initializer found for app " + rowtoprocess.getapp();

        }
    }

    private String updateSlowQueryJobResult(BatchJob rowtoproces, BatchOutput batchOutput) {
        try {
            batchJobService.updateBatchRowSlowQueryoutput(rowtoproces, batchOutput);
            return "";
        } catch (Exception ex) {
            return "failed to update result for app" + rowtoproces.getapp();
        }
    }

    private void updateBatchJobResult(BatchJob rowtoproces, BatchOutput batchOutput) {

    }

    private String processBatch(BatchJob rowtoprocess) {

        return "";
    }

    public class JobProcessor implements Runnable {

        @Override
        public void run() {

            while (bprocessJobs) {

                List<BatchJob> allQueuedBatchRow = batchJobService.getAllQueuedBatchRow(BatchStatus.BatchQueued.name());

                if (allQueuedBatchRow.isEmpty() == true) {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                batchJobService.SwitchBatchToInprogress(allQueuedBatchRow, BatchStatus.BatchInProgress);

            }
        }

    }

    // getOrCreateInitBlock method to retrieve or create an InitBlock for the given
    // app
    public synchronized BatchInitBlock getOrCreateInitBlock(String app) {
        System.out.println("getOrCreateInitBlock method to retrieve or create an InitBlock for the given app...");

        // Synchronize access to ensure thread safety
        synchronized (lock) {

            // Check if an InitBlock already exists for the app
            if (initBlocks.containsKey(app)) {
                return initBlocks.get(app);
            }

            // Check if an Initializer is registered for the app
            BatchInitializer initializer = initializers.get(app);
            if (initializer == null) {
                throw new IllegalStateException("No initializer registered for app: " + app);
            }

            // Create a new InitBlock using the registered Initializer
            BatchInitBlock initBlock = initializer.init(app);

            // Cache the InitBlock for future use
            initBlocks.put(app, initBlock);
            System.out.println("Create a new InitBlock using the registered Initializer...");

            return initBlock;
        }
    }

    // RegisterInitializer method to register an initializer for a specific
    // application
    public synchronized void registerInitializer(String app, BatchInitializer initializer) {
        // Check if an initializer for this app already exists
        if (initializers.containsKey(app)) {
            throw new IllegalStateException("Initializer already registered for app: " + app);
        }
        // Register the initializer for the app
        System.out.println("Register the initializer for the app.......");

        initializers.put(app, initializer);
        System.out.println("Fetch the register initializer for the app......." + initializers.get(app));

    }

    public synchronized void RegisterProcessor(String app, String op, BatchProcessor processor) {

        String key = app + op;
        // Check if an initializer for this app already exists
        if (batchProcessors.containsKey(key)) {
            throw new IllegalStateException("BatchProcessor already registered for app: " + key);
        }
        // Register the initializer for the app
        System.out.println("Register the BatchProcessor for the app.......");

        batchProcessors.put(key, processor);
        System.out.println("Fetch the register BatchProcessor for the app......." + batchProcessors.get(key));

    }

    public synchronized void RegisterProcessor(String app, String op, SQProcessor processor) {

        String key = app + op;
        // Check if an initializer for this app already exists
        if (slowQueryProcessor.containsKey(key)) {
            throw new IllegalStateException("SQProcessor already registered for app: " + key);
        }
        // Register the initializer for the app
        System.out.println("Register the SQProcessor for the app.......");

        slowQueryProcessor.put(key, processor);
        System.out.println("Fetch the register SQProcessor for the app......." + slowQueryProcessor.get(key));

    }

}
