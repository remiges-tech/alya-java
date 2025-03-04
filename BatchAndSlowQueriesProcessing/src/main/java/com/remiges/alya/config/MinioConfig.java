package com.remiges.alya.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.remiges.rigel.service.RigelService;

import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;

@Configuration
public class MinioConfig {

    @Autowired
    private RigelService rigelService;

    private String minioEndpoint;

    private String minioAccessKey;

    private String minioSecretKey;

    @PostConstruct
    public void getValue() {
        minioEndpoint = rigelService.get("NDMLKRA", "KYCEnquiry", "1", "config", "uat/keys", "minioendpoint");
        minioAccessKey = rigelService.get("NDMLKRA", "KYCEnquiry", "1", "config", "uat/keys", "minioaccessKey");
        minioSecretKey = rigelService.get("NDMLKRA", "KYCEnquiry", "1", "config", "uat/keys", "miniosecretKey");
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(minioAccessKey, minioSecretKey)
                .build();
    }
}
