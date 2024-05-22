package com.remiges.alya.service;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioService {

    @Value("${minio.bucket}")
    private String minioBucket;

    @Autowired
    private MinioClient minioClient;

    public String uploadFile(String objectName, InputStream inputStream, long size,
            String contentType) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        try {
            PutObjectArgs arg = PutObjectArgs.builder().bucket(minioBucket).object(objectName)
                    .stream(inputStream, -1, -1).contentType(contentType).build();
            ObjectWriteResponse miniores = minioClient.putObject(arg);
            return miniores.etag();
            // Get object ID (object name)
            // String objectId = minioClient.statObject(minioBucket,
            // objectName).objectName();

        } catch (MinioException e) {
            e.printStackTrace();
            // Handle MinIO exception
        }

        return null;
    }

    // Add methods for other MinIO operations like downloading files, listing
    // objects, etc.
}
