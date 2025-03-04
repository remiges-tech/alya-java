package com.remiges.alya.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.remiges.rigel.service.RigelService;

import jakarta.annotation.PostConstruct;
import redis.clients.jedis.Jedis;

@Configuration
public class AppConfigBatch {

    @Autowired
    private RigelService rigelService;

    private String redisHost;

    private Integer redisPort;

    @PostConstruct
    public void getValue() {

        redisHost = rigelService.get("NDMLKRA", "KYCEnquiry", "1", "config", "uat/keys", "redisHost");
        redisPort = Integer.parseInt(rigelService.get("NDMLKRA", "KYCEnquiry", "1", "config", "uat/keys", "redisPort"));
    }
    
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
