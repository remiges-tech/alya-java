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
public abstract class BatchInitBlocks {
	public abstract boolean isAlive(String appName); // Method to check if the BatchInitBlock object is still valid

	public abstract void close(String appName); // Method to close the BatchInitBlock object
}
