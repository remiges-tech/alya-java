package com.remiges.alya.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for interacting with MinIO object storage.
 */
@Service
public class MinioService {

    @Value("${minio.bucket}")
    private String minioBucket;

    @Autowired
    private MinioClient minioClient;

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);

    /**
     * Uploads a file to the specified bucket in MinIO.
     *
     * @param objectName  the name of the object to be created in the bucket
     * @param file        the file to be uploaded
     * @param contentType the MIME type of the file
     * @return the ETag of the uploaded object
     * @throws InvalidKeyException      if the MinIO credentials are invalid
     * @throws NoSuchAlgorithmException if the algorithm for accessing MinIO is not
     *                                  found
     * @throws IOException              if an I/O error occurs during file upload
     */
    public String uploadFile(String objectName, File file, String contentType)
            throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        try {
            // Check if the bucket already exists
            boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucket).build());
            if (!isBucketExists) {
                // Create the bucket if it doesn't exist
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucket).build());
            }
        } catch (Exception ex) {
            logger.debug("Exception occurred while creating bucket ex: " + ex.toString());
        }

        // InputStream of the file
        try (InputStream inputStream = new FileInputStream(file)) {
            PutObjectArgs arg = PutObjectArgs.builder()
                    .bucket(minioBucket)
                    .object(objectName)
                    .stream(inputStream, file.length(), -1)
                    .contentType(contentType)
                    .build();
            ObjectWriteResponse miniores = minioClient.putObject(arg);
            return miniores.etag();
        } catch (MinioException e) {
            logger.debug("Exception occurred while storing file to bucket ex: " + e.toString());
            // Handle MinIO exception
        }

        return "";
    }

    // Add methods for other MinIO operations like downloading files, listing
    // objects, etc.
}
