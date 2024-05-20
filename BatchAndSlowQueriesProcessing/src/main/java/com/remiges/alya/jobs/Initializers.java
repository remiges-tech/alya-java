package com.remiges.alya.jobs;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

/**
 * The Initializers class manages initialization for different applications.
 */
interface BatchInitBlck {
	boolean isAlive(); // Method to check if the BatchInitBlock object is still valid

	void close(); // Method to close the BatchInitBlock object
}

@Component
public class Initializers {

	private final Map<String, InitBlock> initBlockCache = new HashMap<>();
	private final Logger logger = Logger.getLogger(Initializers.class.getName());

	/**
	 * Registers an initializer for the specified application.
	 * 
	 * @param appName      The name of the application.
	 * @param resourceList List of maps containing resource names as keys and their
	 *                     objects as values.
	 * @return A message indicating the registration status.
	 */
	public String registerInitializer(String appName, Map<String, Object> resourceList) {
		try {
			System.out.println("Registering initializer for app: " + appName);
			// Check if an Initializer is registered for the app
			if (initBlockCache.containsKey(appName)) {
				return "Initializer already registered for app: " + appName;
			}

			// Create or retrieve the InitBlock for the app
			InitBlock initBlock = init(appName, resourceList);

			// Register the initializer
			initBlockCache.put(appName, initBlock);
			return "Initializer registered successfully for app: " + appName;
		} catch (Exception e) {
			// Log and handle the exception
			logger.log(Level.SEVERE, "Failed to register initializer for app: " + appName, e);
			return "Failed to register initializer for app: " + appName + ". See logs for details.";
		}
	}

	/**
	 * Gets or creates the InitBlock for the specified application.
	 * 
	 * @param appName      The name of the application.
	 * @param resourceList List of maps containing resource names as keys and their
	 *                     objects as values.
	 * @return The InitBlock instance.
	 */
	private InitBlock init(String appName, Map<String, Object> resourceList) {
		// Check if an InitBlock already exists for the app
		if (initBlockCache.containsKey(appName)) {
			return initBlockCache.get(appName);
		}

		// Create a new InitBlock
		return new InitBlock(appName, resourceList);
	}

	/**
	 * The InitBlock class encapsulates initialization logic for each application.
	 */
	private static class InitBlock implements BatchInitBlck {

		// Add fields as required for your initialization logic

		/**
		 * Constructor to initialize the InitBlock.
		 * 
		 * @param appName      The name of the application.
		 * @param resourceList List of maps containing resource names as keys and their
		 *                     objects as values.
		 */
		public InitBlock(String appName, Map<String, Object> resourceList) {
			// Implement initialization logic here
			System.out.println("Initializing InitBlock for app: " + appName);
			mergeResources(resourceList);
		}

		/**
		 * Merges resources from a map into a single map.
		 * 
		 * @param resourceList Map containing resource names as keys and their objects
		 *                     as values.
		 * @return The merged resources.
		 */
		private Map<String, Object> mergeResources(Map<String, Object> resourceList) {
			Map<String, Object> mergedResources = new HashMap<>();
			mergedResources.putAll(resourceList);
			return mergedResources;
		}

		/**
		 * Method to check if the BatchInitBlock object is still valid.
		 * 
		 * @return true if the BatchInitBlock is alive, false otherwise.
		 */
		@Override
		public boolean isAlive() {
			// Implement the logic to check if the BatchInitBlock object is still valid
			// For example, check if database handles are still valid
			return true; // Placeholder return value, replace with actual logic
		}

		/**
		 * Method to close the BatchInitBlock object.
		 */
		@Override
		public void close() {
			// Implement the logic to close the BatchInitBlock object
			// For example, close database connections
			System.out.println("Closing InitBlock");
		}
	}
}
