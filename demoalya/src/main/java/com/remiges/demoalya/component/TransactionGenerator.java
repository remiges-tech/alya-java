package com.remiges.demoalya.component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionGenerator {

    public void Generate(String fileName, int numberOfRecords) {

        try (
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            Random random = new Random();
            for (int i = 0; i < numberOfRecords; i++) {
                String record = generateRecord(random);
                writer.write(record);
                writer.newLine();
            }
            System.out.println("File created successfully with " + numberOfRecords + " records.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static String generateRecord(Random random) {
        String type = random.nextBoolean() ? "DEPOSIT" : "WITHDRAWAL";
        double amount = Math.round(random.nextDouble() * 10000); // Generate random amount
        int id = random.nextInt(1000); // Generate random id
        return type + ", " + amount + ", " + id;
    }

}
