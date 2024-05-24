package com.remiges.alya.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.remiges.alya.jobs.BatchStatus;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

@Service
public class JedisService {

    Jedis jedis;
    private static final Logger logger = LoggerFactory.getLogger(JedisService.class);

    JedisService(Jedis jedis) {
        this.jedis = jedis;
    }

    public void updateStatusInRedis(UUID batchID, BatchStatus batchStatus, int expirySec) {
        String redisKey = "ALYA_BATCHSTATUS_" + batchID.toString();
        long expiry = expirySec;

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

}
