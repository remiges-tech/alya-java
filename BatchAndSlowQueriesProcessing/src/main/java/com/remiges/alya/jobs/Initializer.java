package com.remiges.alya.jobs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis; // Import the correct Jedis class

// Define the Initializer interface
interface BatchInitializer {  
    BatchInitBlock init(String app);
}

// Define the abstract class for InitBlock
abstract class BatchInitBlock {
    // Define methods for initialization block
    abstract boolean isAlive();
    abstract void close();
}

// Define the Initializer component
@Component
public class Initializer implements BatchInitializer {

    private final Map<String, BatchInitializer> initializers = new ConcurrentHashMap<>();
    private final Map<String, BatchInitBlock> initBlocks = new ConcurrentHashMap<>();
    private Jedis redisClient;
    private Logger logger;

    public Initializer(Jedis redisClient, Logger logger) {
        this.redisClient = redisClient;
        this.logger = logger;
    }

    @Override
    public BatchInitBlock init(String app) {
        try {
            System.out.println("init block has initialized..." + logger.getName());
            return new InitBlock(redisClient, logger); // Create an instance of InitBlock
        } catch (Exception e) {
            // Handle initialization failure
            System.err.println("Failed to initialize: " + e.getMessage());
            return null; // or throw a custom exception
        }
    }

    // Lock object for synchronization
    private final Object lock = new Object();

    // RegisterInitializer method to register an initializer for a specific application
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

    // getOrCreateInitBlock method to retrieve or create an InitBlock for the given app
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

    // Inner class representing the concrete implementation of BatchInitBlock
    private static class InitBlock extends BatchInitBlock {
        private final Jedis redisClient;
        private final Logger logger;

        public InitBlock(Jedis redisClient, Logger logger) {
            this.redisClient = redisClient;
            this.logger = logger;
        }

        @Override
        boolean isAlive() {
            // Implement the method
            return false;
        }

        @Override
        void close() {
            // Implement the method
        }
    }
}
