package com.remiges.alya.jobs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.remiges.alya.entity.BatchRows;

import lombok.Data;

@Data
public class ProcessOutputToFle {

    private static final Logger logger = LoggerFactory.getLogger(ProcessOutputToFle.class);

    // Map to hold the output files
    Map<String, File> outputFilemap = new HashMap<>();

    /**
     * Generates temporary files for processed batch rows.
     * 
     * @param processedBatchRows the list of processed batch rows
     * @throws IOException if an I/O error occurs
     */
    public void GenerateTempFile(List<BatchRows> processedBatchRows) throws IOException {


        for (BatchRows row : processedBatchRows) {
            Map<String, String> blobrows = row.getBlobrows();

            for (Map.Entry<String, String> entry : blobrows.entrySet()) {
                String filename = entry.getKey(); // Get the filename
                String content = entry.getValue(); // Get the content

                // Create temp file only if content exists
                if (content != null) {
                    try {
                        File file = new File(filename); // Create a File object
                        file.createNewFile(); // Create the file if it doesn't exist

                        outputFilemap.put(filename, file); // Add the file to the map
                    } catch (IOException ex) {
                        logger.debug("Failed to create temp file exception {} ", ex.toString()); // Log the exception
                        throw ex; // Rethrow the exception
                    }
                }
            }
        }
    }

    /**
     * Appends blob rows to their respective files.
     * 
     * @param processedBatchRows the list of processed batch rows
     * @throws IOException if an I/O error occurs
     */
    public void appendBlobRowsToFiles(List<BatchRows> processedBatchRows) throws IOException {

        for (BatchRows row : processedBatchRows) {
            Map<String, String> blobrows = row.getBlobrows();

            for (Map.Entry<String, String> entry : blobrows.entrySet()) {
                String filename = entry.getKey(); // Get the filename
                String content = entry.getValue(); // Get the content

                File file = outputFilemap.get(filename); // Retrieve the file from the map
                if (file != null) {
                    try (FileWriter writer = new FileWriter(file, true)) { // Open the file in append mode
                        writer.write(content); // Write the content to the file
                    } catch (IOException ex) {
                        logger.debug("Failed to write content to file exception {} ", ex.toString()); // Log the
                                                                                                      // exception
                        throw ex; // Rethrow the exception
                    }
                }
            }
        }
    }

    /**
     * Deletes the temporary files.
     */
    public void cleanupTemporaryFiles() {
        outputFilemap.values().forEach(file -> {
            file.delete(); // Delete each file
        });
    }
}