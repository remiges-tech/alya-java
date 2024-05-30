package com.remiges.demoalya.component;

import org.springframework.stereotype.Component;

import com.remiges.alya.jobs.BatchInitBlocks;

import lombok.Data;
import lombok.EqualsAndHashCode;
import redis.clients.jedis.Jedis;

@Data
@EqualsAndHashCode(callSuper = false)
public class TransactionInitBlock extends BatchInitBlocks {

    Jedis jedis;

    TransactionInitBlock(redis.clients.jedis.Jedis jedis2) {
        this.jedis = jedis2;

        jedis.set(Constants.RedisKey, String.valueOf(0));

    }

    @Override
    public boolean isAlive(String appName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAlive'");
    }

    @Override
    public void close(String appName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }

}
