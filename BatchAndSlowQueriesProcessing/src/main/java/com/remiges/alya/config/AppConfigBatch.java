package com.remiges.alya.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.Jedis;

@Configuration
public class AppConfigBatch {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private Integer redisPort;

    @Bean
    public Jedis jedis() {
        return new Jedis(redisHost, redisPort);
    }

    /*
     * @Bean
     * public JobMgr myComponent(BatchJobService myService) {
     * return new JobMgr();
     * }
     */
}
