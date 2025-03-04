package com.remiges.alya.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.remiges.rigel.service.RigelService;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
/**
 * Service class for interacting with MinIO object storage.
 */
@Service
@Slf4j
public class MinioService {

    @Autowired
    private RigelService rigelService;

    private String minioBucket;

    private String nfsBaseDirectory;

    private boolean useMinio;
    
    @PostConstruct
    public void getValue() {
        minioBucket = rigelService.get("NDMLKRA", "KYCEnquiry", "1", "config", "uat/keys", "miniobucket");
        useMinio = Boolean.parseBoolean(rigelService.get("NDMLKRA", "KYCEnquiry", "1", "config", "uat/keys", "true"));
        nfsBaseDirectory = rigelService.get("NDMLKRA", "KYCEnquiry", "1", "config", "uat/keys", "nfsBaseDirectory");
        log.info("creating directory alya batch: while intialilze{}", nfsBaseDirectory);
        useMinio = false;
    }

    @Autowired
    private MinioClient minioClient;

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);

    /**
     * Uploads a file to MinIO or NFS based on the configuration flag.
     *
     * @param objectName  the name of the object to be created in the MinIO or NFS
     * @param file        the file to be uploaded
     * @param contentType the MIME type of the file
     * @return the ETag of the uploaded object  or NFS file path
     * @throws InvalidKeyException      if the MinIO credentials are invalid
     * @throws NoSuchAlgorithmException if the algorithm for accessing MinIO is not
     *                                  found
     * @throws IOException              if an I/O error occurs during file upload
     */
    public String uploadFile(String objectName, File file, String contentType)
            throws InvalidKeyException, NoSuchAlgorithmException, IOException {
                if (useMinio) {
                    return uploadFileToMinio(objectName, file, contentType);
                } else {
                    return uploadFileToNfs(objectName, file);
                }
            }
    private String uploadFileToMinio(String objectName, File file, String contentType)
            throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        try {
            boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucket).build());
            if (!isBucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucket).build());
            }
        } catch (Exception ex) {
            logger.debug("Exception occurred while creating bucket ex: " + ex.toString());
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            PutObjectArgs arg = PutObjectArgs.builder()
                    .bucket(minioBucket)
                    .object(objectName)
                    .stream(inputStream, file.length(), -1)
                    .contentType(contentType)
                    .build();
            ObjectWriteResponse minioResponse = minioClient.putObject(arg);
            return minioResponse.etag();
        } catch (MinioException e) {
            logger.debug("Exception occurred while storing file to bucket ex: " + e.toString());
        }

        return "";
    }
    
    private String uploadFileToNfs(String objectName, File file) throws IOException {
        Path directoryPath = Paths.get(nfsBaseDirectory, minioBucket);
        Path filePath = directoryPath.resolve(objectName);

        log.info("File uploaded successfully to NFS directory by alya batch: {}", filePath);

        try {
            // Create directories if they do not exist
            if (!Files.exists(directoryPath)) {
                log.info("creating directory alya batch: {}", directoryPath);
                Files.createDirectories(directoryPath);
            }

            // Copy the input stream to the NFS directory
            Files.copy(new FileInputStream(file), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File uploaded successfully to NFS directory by Alya after copy: {}", filePath);
            return filePath.toString();
        } catch (IOException e) {
            logger.error("Error uploading file to NFS by Alya: {}", e.getMessage());
            throw new IOException("Failed to upload file to NFS by Alya", e);
        }
    }

    /**
     * Uploads a Base64 encoded blob to MinIO or NFS based on the configuration
     * flag.
     *
     * @param objectName   the name of the object to be created in MinIO or NFS
     * @param base64String the Base64 encoded string of the file
     * @param contentType  the MIME type of the file
     * @return the location of the uploaded object or file
     * @throws InvalidKeyException      if the MinIO credentials are invalid
     * @throws NoSuchAlgorithmException if the algorithm for accessing MinIO is not
     *                                  found
     * @throws IOException              if an I/O error occurs during file upload
     */
    public String uploadBlob(String objectName, String base64String, String contentType)
            throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        if (useMinio) {
            return uploadBlobToMinio(objectName, base64String, contentType);
        } else {
            return uploadBlobToNfs(objectName, base64String);
        }
    }

    private String uploadBlobToMinio(String objectName, String base64String, String contentType)
            throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        try {
            boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucket).build());
            if (!isBucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucket).build());
            }
        } catch (Exception ex) {
            logger.debug("Exception occurred while creating bucket ex: " + ex.toString());
        }

        byte[] binaryData = Base64.getDecoder().decode(base64String);

        try (InputStream inputStream = new ByteArrayInputStream(binaryData)) {
            PutObjectArgs arg = PutObjectArgs.builder()
                    .bucket(minioBucket)
                    .object(objectName)
                    .stream(inputStream, binaryData.length, -1)
                    .contentType(contentType)
                    .build();
            ObjectWriteResponse minioResponse = minioClient.putObject(arg);
            return minioBucket + "/" + objectName;
        } catch (MinioException e) {
            logger.debug("Exception occurred while storing blob to bucket ex: " + e.toString());
        }

        return "No Object Id has Mapped";
    }
    private String uploadBlobToNfs(String objectName, String base64String) throws IOException {
        byte[] binaryData = Base64.getDecoder().decode(base64String);
        Path directoryPath = Paths.get(nfsBaseDirectory);
        Path filePath = directoryPath.resolve(objectName);

        try {
            // Create directories if they do not exist
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // Write the blob data to the NFS file
            try (InputStream inputStream = new ByteArrayInputStream(binaryData)) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Blob uploaded successfully to NFS directory: {}", filePath);
                return filePath.toString();
            }
        } catch (IOException e) {
            logger.error("Error uploading blob to NFS: {}", e.getMessage());
            throw new IOException("Failed to upload blob to NFS", e);
        }
    }

}