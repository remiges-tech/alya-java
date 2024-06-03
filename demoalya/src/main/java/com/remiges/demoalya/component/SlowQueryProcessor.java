package com.remiges.demoalya.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.remiges.alya.jobs.BatchInitBlocks;
import com.remiges.alya.jobs.BatchOutput;
import com.remiges.alya.jobs.BatchStatus;
import com.remiges.alya.jobs.ErrorCodes;
import com.remiges.alya.jobs.SQProcessor;

import redis.clients.jedis.Jedis;

public class SlowQueryProcessor extends SQProcessor {

	@Override
	public BatchOutput DoSlowQuery(BatchInitBlocks initBlock, JsonNode context, String input) {
		Map<String, String> blobRows = new HashMap<>();
		String messages = "";

		SlowQueryInitBlock slowQueryInitializer = (SlowQueryInitBlock) initBlock;

		Jedis jedis = slowQueryInitializer.getRedisConnection();
		Connection dbConnection = slowQueryInitializer.getDatabaseConnection();

		try {
			// Execute the PostgreSQL query
			try (PreparedStatement statement = dbConnection.prepareStatement(input)) {
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						// Assuming the result contains columns "id" and "output"
						String id = resultSet.getString("id");
						String output = resultSet.getString("output");
						blobRows.put(id, output);
					}
				}
			}
		} catch (SQLException e) {
			messages = "Error executing SQL query: " + e.getMessage();
			e.printStackTrace(); // Log the exception for debugging
		}

		// Now you have the query output stored in the blobRows map
		// You can perform further processing or return it as needed

		return new BatchOutput(BatchStatus.BatchSuccess, null, null, blobRows, ErrorCodes.NOERROR);
	}
}
