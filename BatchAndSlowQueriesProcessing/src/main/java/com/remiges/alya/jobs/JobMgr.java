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
    private final Map<String, SQProcessor> SQProcessor = new ConcurrentHashMap<>();

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

    public void processRow(BatchJob rowtoprocess) {

    }

    private void processSlowQuery(BatchJob rowtoprocess) {

    }

    private void processBatch(BatchJob rowtoprocess) {

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
        if (SQProcessor.containsKey(key)) {
            throw new IllegalStateException("SQProcessor already registered for app: " + key);
        }
        // Register the initializer for the app
        System.out.println("Register the SQProcessor for the app.......");

        SQProcessor.put(key, processor);
        System.out.println("Fetch the register SQProcessor for the app......." + SQProcessor.get(key));

    }

}
