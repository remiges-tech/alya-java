package com.remiges.alya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.remiges.alya.jobs.JobMgr;
import com.remiges.alya.service.BatchJobService;

import redis.clients.jedis.Jedis;

@Configuration
public class AppConfig {

    @Bean
    public static Jedis jedis() {
        return new Jedis("localhost", 6379);
    }

    /*
     * @Bean
     * public JobMgr myComponent(BatchJobService myService) {
     * return new JobMgr();
     * }
     */
}
