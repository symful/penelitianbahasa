package com.scheduling.util;

import com.scheduling.model.Task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {

    private static final String[] TASK_NAMES = {
        "Tugas Pemrograman Web", "Tugas Basis Data", "Tugas Algoritma",
        "Tugas Jaringan Komputer", "Tugas Kriptografi", "Tugas Statistik",
        "Tugas Machine Learning", "Tugas UI/UX Design", "Tugas Proyek Akhir",
        "Tugas Pengantar AI", "Tugas Matematika Diskrit", "Tugas Sistem Operasi",
        "Tugas Grafika Komputer", "Tugas Keamanan Jaringan",
        "Tugas Rekayasa Perangkat Lunak", "Tugas Cloud Computing",
        "Tugas Internet of Things", "Tugas Big Data", "Tugas Blockchain",
        "Tugas Augmented Reality", "Tugas Text Mining", "Tugas Neural Network",
        "Tugas Computer Vision", "Tugas Natural Language Processing",
        "Tugas Distributed Systems", "Tugas DevOps Practices",
        "Tugas Microservices", "Tugas Quantum Computing",
        "Tugas Embedded Systems", "Tugas Mobile Development"
    };

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final long SEED = 42L;

    public static List<Task> generateRandomTasks(int count) {
        return generateRandomTasks(count, SEED);
    }

    public static List<Task> generateRandomTasks(int count, long seed) {
        Random random = new Random(seed);
        List<Task> tasks = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 1; i <= count; i++) {
            String name = TASK_NAMES[random.nextInt(TASK_NAMES.length)];
            String studentId = "S" + String.format("%03d", random.nextInt(100) + 1);
            int lksCount = random.nextInt(10) + 1;
            int difficulty = random.nextInt(5) + 1;
            int daysAhead = random.nextInt(30) + 1;
            int hour = random.nextInt(12) + 8;
            int minute = random.nextInt(4) * 15;
            LocalDateTime deadline = now.plusDays(daysAhead).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
            int estimatedMinutes = random.nextInt(211) + 30;

            Task task = new Task(i, name, studentId, lksCount, difficulty, deadline, estimatedMinutes);
            tasks.add(task);
        }

        return tasks;
    }

    public static String toCsvLine(Task task) {
        return String.format("%d,%s,%s,%d,%d,%s,%d",
                task.getId(),
                task.getName(),
                task.getStudentId(),
                task.getLksCount(),
                task.getDifficulty(),
                task.getDeadline().format(FORMATTER),
                task.getEstimatedMinutes());
    }
}