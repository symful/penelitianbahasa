package com.scheduling.util;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.scheduling.model.Task;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class CsvLoader {

    private static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private CsvLoader() {
        // Utility class, no instantiation
    }

    public static List<Task> loadFromFile(String path) {
        return loadFromFile(Path.of(path));
    }

    public static List<Task> loadFromFile(Path path) {
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(path)) {
            System.err.println("Warning: File not found: " + path);
            return tasks;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            var csvReader = new CSVReaderBuilder(reader)
                    .build();

            String[] header = csvReader.readNext();
            if (header == null) {
                return tasks; // Empty file
            }

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                try {
                    Task task = parseTask(line);
                    tasks.add(task);
                } catch (Exception e) {
                    System.err.println("Warning: Skipping malformed row: " + String.join(",", line) + " - " + e.getMessage());
                }
            }

        } catch (IOException | CsvValidationException e) {
            System.err.println("Warning: Error reading CSV file: " + e.getMessage());
        }

        return tasks;
    }

    private static Task parseTask(String[] fields) throws Exception {
        if (fields.length < 7) {
            throw new IllegalArgumentException("Expected 7 fields, got " + fields.length);
        }

        int id = Integer.parseInt(fields[0].trim());
        String name = fields[1].trim();
        String studentId = fields[2].trim();
        int lksCount = Integer.parseInt(fields[3].trim());
        int difficulty = Integer.parseInt(fields[4].trim());
        LocalDateTime deadline = LocalDateTime.parse(fields[5].trim(), DEADLINE_FORMATTER);
        int estimatedMinutes = Integer.parseInt(fields[6].trim());

        return new Task(id, name, studentId, lksCount, difficulty, deadline, estimatedMinutes);
    }
}