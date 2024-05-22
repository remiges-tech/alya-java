package com.remiges.alya.jobs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.remiges.alya.entity.BatchRows;

import lombok.Data;

@Component
@Data
public class ProcessOutputToFle {

    Map<String, File> outputFilemap;

    public void GenerateTempFile(List<BatchRows> processedBatchRows) {

        processedBatchRows.stream().forEach(row -> {
            Map<String, String> blobrows = row.getBlobrows();

            blobrows.forEach((filename, content) -> {
                try { // Create a File object representing the file
                    File file = new File(filename);
                    // Create the file if it doesn't exist
                    if (file.createNewFile()) {
                        System.out.println("File created: " + file.getAbsolutePath());
                        outputFilemap.put(filename, file);
                    }
                } catch (IOException ex) {

                }
            });

        });
    }

    public String appendBlobRowsToFiles(List<BatchRows> processedBatchRows) {

        processedBatchRows.stream().forEach(row -> {
            Map<String, String> blobrows = row.getBlobrows();

            blobrows.forEach((filename, content) -> {
                File file = outputFilemap.get(filename);

                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.write(content);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            });
        });

        return "";
    }
}
