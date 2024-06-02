package com.remiges.demoalya.component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.remiges.alya.jobs.BatchInitBlocks;

import redis.clients.jedis.Jedis;

public class SlowQueryInitBlock extends BatchInitBlocks {

	private Connection databaseConnection; // Database connection
	private Jedis redisConnection; // Redis connection

	// Database connection parameters
	private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
	private static final String DB_USER = "postgres";
	private static final String DB_PASSWORD = "root";

	// Redis connection parameters
	private static final String REDIS_HOST = "localhost";
	private static final int REDIS_PORT = 6379;

	public SlowQueryInitBlock() {
		// Initialize database connection
		try {
			this.databaseConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace(); // Handle connection errors
		}

		// Initialize Redis connection
		this.redisConnection = new Jedis(REDIS_HOST, REDIS_PORT);
	}

	@Override
	public boolean isAlive(String appName) {
		// Implement logic to check if both database and Redis connections are alive
		return databaseConnection != null && redisConnection.isConnected();
	}

	@Override
	public void close(String appName) {
		// Close database connection
		if (databaseConnection != null) {
			try {
				databaseConnection.close();
			} catch (SQLException e) {
				e.printStackTrace(); // Handle connection closing errors
			}
		}

		// Close Redis connection
		if (redisConnection != null) {
			redisConnection.close();
		}
	}

	// Optionally, provide getter methods to access the connections externally
	public Connection getDatabaseConnection() {
		return databaseConnection;
	}

	public Jedis getRedisConnection() {
		return redisConnection;
	}
}
