package com.remiges.demoalya.component;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remiges.alya.jobs.BatchInitBlocks;
import com.remiges.alya.jobs.BatchOutput;
import com.remiges.alya.jobs.BatchProcessor;
import com.remiges.alya.jobs.BatchStatus;
import com.remiges.alya.jobs.ErrorCodes;
import com.remiges.demoalya.TransactionInput;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class TransactionProcessor implements BatchProcessor {

    @Override
    public BatchOutput DoBatchJob(BatchInitBlocks any, JsonNode context, int line, String input) {
        // TODO Auto-generated method stub
        Map<String, String> blobRows = new HashMap<>();
        Map<String, String> result = new HashMap<>();
        Map<String, String> messages = new HashMap<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TransactionInput txInput = objectMapper.readValue(input, TransactionInput.class);
            TransactionInitBlock transinit = (TransactionInitBlock) any;
            Jedis jedis = transinit.getJedis();

            try {
                updateBalanceInRedis(jedis, txInput);

                blobRows.put("transaction_summary.txt",
                        "Trans Id: " + txInput.getId() + " Amount : " + txInput.getAmount());
                result.put("res", "Success");
            } catch (Exception ex) {
                messages.put("Err", ex.getMessage());
                messages.put("Err2", "failed to update balance in redis");

                return new BatchOutput(BatchStatus.BatchFailed, null, messages, null, ErrorCodes.ERROR);

            }

        } catch (Exception ex) {
            messages.put("Err", ex.getMessage());
            messages.put("Err2", "failed to process transaction  ");

            return new BatchOutput(BatchStatus.BatchFailed, null, messages, null, ErrorCodes.ERROR);

        }

        return new BatchOutput(BatchStatus.BatchSuccess, result, null, blobRows, ErrorCodes.NOERROR);

    }

    public void updateBalanceInRedis(Jedis jedis, TransactionInput txInput) {
        int maxRetries = 3;
        int retry;
        String balanceKey = Constants.RedisKey;

        for (retry = 0; retry < maxRetries; retry++) {
            Transaction transaction = null;

            try {
                String balvalue = jedis.get(balanceKey);
                jedis.watch(balanceKey);
                transaction = jedis.multi();
                double balance = Double.parseDouble(balvalue);
                double newBalance = 0;
                if (txInput.getType().equals("DEPOSIT")) {
                    newBalance = balance + txInput.getAmount();
                } else if (txInput.getType().equals("WITHDRAWAL")) {
                    newBalance = balance - txInput.getAmount();
                }
                transaction.set(balanceKey, Double.toString(newBalance));
                transaction.exec();
                break;
            } catch (Exception e) {
                // Log or handle the error
                e.printStackTrace();
                throw e;
            } finally {
                // Always unwatch after the transaction
                // Always unwatch after the transaction
                if (transaction != null) {
                    transaction.close();
                }
                jedis.unwatch();
            }
        }
    }

}
