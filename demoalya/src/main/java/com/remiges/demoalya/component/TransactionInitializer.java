package com.remiges.demoalya.component;

import com.remiges.alya.jobs.BatchInitBlocks;
import com.remiges.alya.jobs.Initializer;

import redis.clients.jedis.Jedis;

public class TransactionInitializer extends Initializer {

    @Override
    public BatchInitBlocks init(String app) {

        return new TransactionInitBlock(new Jedis("localhost", 6379));

    }

}
