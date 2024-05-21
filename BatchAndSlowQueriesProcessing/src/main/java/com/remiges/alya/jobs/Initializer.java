package com.remiges.alya.jobs;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

/**
 * The Initializers class manages initialization for different applications.
 */
abstract class BatchInitBlocks {
	abstract boolean isAlive(String appName); // Method to check if the BatchInitBlock object is still valid

	abstract void close(String appName); // Method to close the BatchInitBlock object
}

@Component
public class Initializer {



	private final Logger logger = Logger.getLogger(Initializer.class.getName());

	/**
	 * Gets or creates the InitBlock for the specified application.
	 * 
	 * @param appName      The name of the application.
	 * @param resourceList List of maps containing resource names as keys and their
	 *                     objects as values.
	 * @return The InitBlock instance.
	 */
	public InitBlock init(String appName) {

		// Create a new InitBlock
		InitBlock initBlock = new InitBlock(appName);

		return initBlock;
	}



	/**
	 * The InitBlock class encapsulates initialization logic for each application.
	 */
	public static class InitBlock extends BatchInitBlocks {

		// Add fields as required for your initialization logic

		/**
		 * Constructor to initialize the InitBlock.
		 * 
		 * @param appName      The name of the application.
		 * @param resourceList List of maps containing resource names as keys and their
		 *                     objects as values.
		 */
		public InitBlock(String appName) {
			// Implement initialization logic here
			System.out.println("Initializing InitBlock for app: " + appName);

		}

		/*
		 * Method to check if the BatchInitBlock object is still valid.
		 * 
		 * @return true if the BatchInitBlock is alive, false otherwise.
		 */
		@Override
		public synchronized boolean isAlive(String appName) {
			return false;
		}

		/**
		 * Method to close the BatchInitBlock object.
		 */
		@Override
		public synchronized void close(String appName) {
			// Implement the logic to close the BatchInitBlock object
			// For example, close database connections
			System.out.println("Closing InitBlock");
		}
	}
}
