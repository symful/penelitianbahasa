package com.scheduling.util;

import com.scheduling.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvLoaderTest {

    @TempDir
    Path tempDir;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void loadFromFile_WithValidCsv_ReturnsNonEmptyList() throws IOException {
        // Create a valid CSV file
        Path csvFile = tempDir.resolve("valid_tasks.csv");
        String csvContent = """
                id,name,studentId,lksCount,difficulty,deadline,estimatedMinutes
                1,Task One,S001,5,3,2026-12-31T23:59,120
                2,Task Two,S002,10,5,2026-06-15T12:00,60
                """;
        Files.writeString(csvFile, csvContent);

        List<Task> tasks = CsvLoader.loadFromFile(csvFile.toString());

        assertNotNull(tasks);
        assertFalse(tasks.isEmpty(), "Should return non-empty list for valid CSV");
        assertEquals(2, tasks.size(), "Should have 2 tasks from CSV");
    }

    @Test
    void loadFromFile_WithEmptyCsv_ReturnsEmptyList() throws IOException {
        // Create an empty CSV file (header only)
        Path csvFile = tempDir.resolve("empty_tasks.csv");
        String csvContent = "id,name,studentId,lksCount,difficulty,deadline,estimatedMinutes\n";
        Files.writeString(csvFile, csvContent);

        List<Task> tasks = CsvLoader.loadFromFile(csvFile.toString());

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty(), "Should return empty list for CSV with only header");
    }

    @Test
    void loadFromFile_WithNonexistentFile_ReturnsEmptyList() {
        List<Task> tasks = CsvLoader.loadFromFile("nonexistent_file.csv");

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty(), "Should return empty list for nonexistent file");
    }

    @Test
    void loadFromFile_WithMalformedRows_SkipsInvalidRows() throws IOException {
        // Create CSV with one valid row and one malformed row
        Path csvFile = tempDir.resolve("mixed_tasks.csv");
        String csvContent = """
                id,name,studentId,lksCount,difficulty,deadline,estimatedMinutes
                1,Task One,S001,5,3,2026-12-31T23:59,120
                2,Task Two,S002,invalid,difficulty,2026-06-15T12:00,60
                """;
        Files.writeString(csvFile, csvContent);

        List<Task> tasks = CsvLoader.loadFromFile(csvFile.toString());

        assertNotNull(tasks);
        assertEquals(1, tasks.size(), "Should return only valid tasks, skipping malformed rows");
    }

    @Test
    void loadFromFile_ParsesTaskFieldsCorrectly() throws IOException {
        // Create a valid CSV file
        Path csvFile = tempDir.resolve("detailed_tasks.csv");
        String csvContent = """
                id,name,studentId,lksCount,difficulty,deadline,estimatedMinutes
                42,Math Homework,S042,8,4,2026-08-15T14:30,90
                """;
        Files.writeString(csvFile, csvContent);

        List<Task> tasks = CsvLoader.loadFromFile(csvFile.toString());

        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        assertEquals(42, task.getId());
        assertEquals("Math Homework", task.getName());
        assertEquals("S042", task.getStudentId());
        assertEquals(8, task.getLksCount());
        assertEquals(4, task.getDifficulty());
        assertEquals(90, task.getEstimatedMinutes());
    }
}
