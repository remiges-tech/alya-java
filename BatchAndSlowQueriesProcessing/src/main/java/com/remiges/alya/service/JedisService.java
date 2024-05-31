package com.remiges.alya.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.remiges.alya.config.JobManagerConfig;
import com.remiges.alya.jobs.BatchStatus;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

@Service
public class JedisService {

	Jedis jedis;
	private static final Logger logger = LoggerFactory.getLogger(JedisService.class);

	JobManagerConfig mgrConfig;

	JedisService(Jedis jedis, JobManagerConfig mgrConfig)

	{
		this.mgrConfig = mgrConfig;
		this.jedis = jedis;
	}

	public void updateStatusInRedis(UUID batchID, BatchStatus batchStatus) {
		String redisKey = "ALYA_BATCHSTATUS_" + batchID.toString();
		long expiry = mgrConfig.getALYA_BATCHSTATUS_CACHEDUR_SEC() * 100;

		try {
			jedis.watch(redisKey);
			String currentStatus = jedis.get(redisKey);
			if (currentStatus != null && currentStatus.equals(batchStatus.toString())) {
				jedis.unwatch();
				return;
			}

			Transaction tx = jedis.multi();
			tx.setex(redisKey, expiry, batchStatus.toString());
			tx.exec();
		} catch (Exception e) {

			logger.debug("Error updating status in Redisfor key {} ex {}", redisKey, e.getMessage());
			// Handle exceptions
			throw e;

		} finally {
			jedis.unwatch();
		}
	}

	// Implementation of lookupRedis method
	public String getBatchStatusFromRedis(String key) {
		try {
			// Check if the key exists in Redis
			if (jedis.exists(key)) {
				// Retrieve the value corresponding to the key
				return jedis.get(key);
			} else {
				// If the key doesn't exist, return null
				return null;
			}
		} catch (Exception e) {
			// Log any exceptions that occur during the Redis lookup
			e.printStackTrace();
			return null;
		}
	}

}
