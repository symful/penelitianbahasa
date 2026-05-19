package com.scheduling.util;

import com.scheduling.model.Task;
import com.scheduling.scheduler.HeapScheduler;
import com.scheduling.scheduler.LinearScheduler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BenchmarkReportGenerator {

    private static final int[] TASK_COUNTS = {10, 50, 100, 500};
    private static final int WARMUP_RUNS = 3;
    private static final int MEASUREMENT_RUNS = 3;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private BenchmarkReportGenerator() {
        // Utility class
    }

    public static void generateReport(String outputPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writeHeader(writer);
            writeSystemInfo(writer);
            writeBenchmarkTable(writer);
        }
    }

    private static void writeHeader(PrintWriter writer) {
        writer.println("=".repeat(90));
        writer.println("                    SCHEDULER BENCHMARK REPORT");
        writer.println("=".repeat(90));
        writer.println();
    }

    private static void writeSystemInfo(PrintWriter writer) {
        writer.println("SYSTEM INFORMATION");
        writer.println("-".repeat(90));
        writer.printf("  Java Version:     %s%n", System.getProperty("java.version"));
        writer.printf("  Java Vendor:      %s%n", System.getProperty("java.vendor"));
        writer.printf("  OS Name:          %s%n", System.getProperty("os.name"));
        writer.printf("  OS Version:       %s%n", System.getProperty("os.version"));
        writer.printf("  OS Arch:          %s%n", System.getProperty("os.arch"));
        writer.printf("  Available CPUs:   %d%n", Runtime.getRuntime().availableProcessors());
        writer.printf("  Max Memory:       %d MB%n", Runtime.getRuntime().maxMemory() / (1024 * 1024));
        writer.printf("  Generated At:     %s%n", LocalDateTime.now().format(FORMATTER));
        writer.println();
    }

    private static void writeBenchmarkTable(PrintWriter writer) {
        writer.println("BENCHMARK RESULTS");
        writer.println("-".repeat(90));
        writer.println();
        writer.printf("  Task counts tested: %s%n", formatTaskCounts());
        writer.printf("  Warmup runs:       %d%n", WARMUP_RUNS);
        writer.printf("  Measurement runs:  %d (averaged)%n", MEASUREMENT_RUNS);
        writer.println();
        writer.println();

        // Table header
        writer.printf("%-12s | %-20s | %-20s | %-15s | %-15s%n",
                "Task Count", "Heap Total (ms)", "Linear Total (ms)", "Speedup Ratio", "Ops/sec (Heap)");
        writer.println("-".repeat(90));

        for (int taskCount : TASK_COUNTS) {
            BenchmarkResult result = runBenchmarkForCount(taskCount);
            double opsPerSec = result.heapTotalNs > 0
                ? (taskCount * MEASUREMENT_RUNS * 1_000_000_000.0 / result.heapTotalNs)
                : 0;

            writer.printf("%-12d | %-20.4f | %-20.4f | %-15.4f | %-15.0f%n",
                    taskCount,
                    result.heapTotalMs,
                    result.linearTotalMs,
                    result.speedup,
                    opsPerSec);
        }

        writer.println();
        writer.println();

        // Detailed breakdown section
        writeDetailedBreakdown(writer);
    }

    private static void writeDetailedBreakdown(PrintWriter writer) {
        writer.println("DETAILED BREAKDOWN BY TASK COUNT");
        writer.println("=".repeat(90));
        writer.println();

        for (int taskCount : TASK_COUNTS) {
            writer.println("Task Count: " + taskCount);
            writer.println("-".repeat(50));

            BenchmarkResult result = runBenchmarkForCount(taskCount);

            writer.printf("  Heap Scheduler (PriorityQueue):%n");
            writer.printf("    Total time:   %12.4f ms%n", result.heapTotalMs);
            writer.printf("    Per task:     %12.6f ms%n", result.heapTotalMs / taskCount);
            writer.println();

            writer.printf("  Linear Scheduler (ArrayList):%n");
            writer.printf("    Total time:   %12.4f ms%n", result.linearTotalMs);
            writer.printf("    Per task:     %12.6f ms%n", result.linearTotalMs / taskCount);
            writer.println();

            writer.printf("  Speedup Ratio (Linear/Heap): %.4fx%n", result.speedup);
            if (result.speedup > 1.0) {
                writer.printf("  Result: Heap is %.2fx faster than Linear%n", result.speedup);
            } else {
                writer.printf("  Result: Linear is %.2fx faster than Heap%n", 1.0 / result.speedup);
            }
            writer.println();
            writer.println();
        }
    }

    private static String formatTaskCounts() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TASK_COUNTS.length; i++) {
            sb.append(TASK_COUNTS[i]);
            if (i < TASK_COUNTS.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private static BenchmarkResult runBenchmarkForCount(int taskCount) {
        List<Task> tasks = generateRandomTasks(taskCount);

        // Warm up
        for (int i = 0; i < WARMUP_RUNS; i++) {
            runHeapScheduler(new ArrayList<>(tasks));
            runLinearScheduler(new ArrayList<>(tasks));
        }

        // Measure Heap
        long heapTotalNs = 0;
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            List<Task> taskCopy = new ArrayList<>(tasks);
            long start = System.nanoTime();
            runHeapScheduler(taskCopy);
            long end = System.nanoTime();
            heapTotalNs += (end - start);
        }

        // Measure Linear
        long linearTotalNs = 0;
        for (int i = 0; i < MEASUREMENT_RUNS; i++) {
            List<Task> taskCopy = new ArrayList<>(tasks);
            long start = System.nanoTime();
            runLinearScheduler(taskCopy);
            long end = System.nanoTime();
            linearTotalNs += (end - start);
        }

        double heapAvgMs = (heapTotalNs / (double) MEASUREMENT_RUNS) / 1_000_000.0;
        double linearAvgMs = (linearTotalNs / (double) MEASUREMENT_RUNS) / 1_000_000.0;
        double speedup = linearAvgMs / heapAvgMs;

        return new BenchmarkResult(heapAvgMs, linearAvgMs, speedup, heapTotalNs);
    }

    private static void runHeapScheduler(List<Task> tasks) {
        HeapScheduler scheduler = new HeapScheduler();
        for (Task task : tasks) {
            scheduler.addTask(task);
        }
        while (scheduler.pollNextTask() != null) {
            // Poll all tasks
        }
    }

    private static void runLinearScheduler(List<Task> tasks) {
        LinearScheduler scheduler = new LinearScheduler();
        for (Task task : tasks) {
            scheduler.addTask(task);
        }
        while (scheduler.pollNextTask() != null) {
            // Poll all tasks
        }
    }

    private static List<Task> generateRandomTasks(int count) {
        List<Task> tasks = new ArrayList<>(count);
        Random random = new Random(42);
        LocalDateTime baseTime = LocalDateTime.now();
        for (int i = 0; i < count; i++) {
            tasks.add(new Task(
                    i,
                    "Task-" + i,
                    "STU-" + random.nextInt(1000),
                    random.nextInt(10) + 1,
                    random.nextInt(5) + 1,
                    baseTime.plusDays(random.nextInt(30)),
                    random.nextInt(120) + 30
            ));
        }
        return tasks;
    }

    private static class BenchmarkResult {
        final double heapTotalMs;
        final double linearTotalMs;
        final double speedup;
        final long heapTotalNs;

        BenchmarkResult(double heapTotalMs, double linearTotalMs, double speedup, long heapTotalNs) {
            this.heapTotalMs = heapTotalMs;
            this.linearTotalMs = linearTotalMs;
            this.speedup = speedup;
            this.heapTotalNs = heapTotalNs;
        }
    }
}