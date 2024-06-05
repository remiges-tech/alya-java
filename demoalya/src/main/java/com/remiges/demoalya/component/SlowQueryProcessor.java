package com.remiges.demoalya.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.jobs.BatchInitBlocks;
import com.remiges.alya.jobs.BatchOutput;
import com.remiges.alya.jobs.BatchStatus;
import com.remiges.alya.jobs.ErrorCodes;
import com.remiges.alya.jobs.SQProcessor;

import redis.clients.jedis.Jedis;

public class SlowQueryProcessor extends SQProcessor {

	@Override
	public BatchOutput DoSlowQuery(SlowQueryInitBlock initBlock, JsonNode context, String input) {
		Map<String, String> blobMap = new HashMap<>();
		String messages = "";
		Map<String, String> rowMap = null;
		SlowQueryInitBlock slowQueryInitializer = (SlowQueryInitBlock) initBlock;

		Jedis jedis = slowQueryInitializer.getRedisConnection();
		Connection dbConnection = slowQueryInitializer.getDatabaseConnection();
		String fileName = generateUniqueFileName();

		try {
			// Execute the PostgreSQL query
			try (PreparedStatement statement = dbConnection.prepareStatement(input)) {
				try (ResultSet resultSet = statement.executeQuery()) {
					ResultSetMetaData metaData = resultSet.getMetaData();
					int columnCount = metaData.getColumnCount();

					while (resultSet.next()) {
						// Generate unique file name

						// Create a map to store the column names and values dynamically
						rowMap = new HashMap<>();
						for (int i = 1; i <= columnCount; i++) {
							String columnName = metaData.getColumnName(i);
							String columnValue = resultSet.getString(i);
							rowMap.put(columnName, columnValue);
						}

					}
				}
			}
			blobMap.put("SlowQueryResultFile", rowMap.toString());
		} catch (SQLException e) {
			messages = "Error executing SQL query: " + e.getMessage();
			e.printStackTrace(); // Log the exception for debugging
		}

		// Now you have the query output stored in the blobMap map
		// You can perform further processing or return it as needed

		return new BatchOutput(BatchStatus.BatchSuccess, null, null, null, ErrorCodes.NOERROR);
	}

	// Method to generate a unique file name
	private String generateUniqueFileName() {
		return System.currentTimeMillis() + "_" + UUID.randomUUID().toString();
	}
}
