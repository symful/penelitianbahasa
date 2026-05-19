package com.scheduling;

import com.scheduling.model.Task;
import com.scheduling.scheduler.HeapScheduler;
import com.scheduling.scheduler.LinearScheduler;
import com.scheduling.util.BenchmarkReportGenerator;
import com.scheduling.util.CsvLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Main {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Main() {
        // Entry point class, no instantiation
    }

    public static void main(String[] args) {
        if (args.length == 0 || containsArg(args, "--help")) {
            printUsage();
            return;
        }

        String inputPath = getArgValue(args, "--input");
        String generateStr = getArgValue(args, "--generate");
        String schedulerStr = getArgValue(args, "--scheduler");
        boolean benchmark = containsArg(args, "--benchmark");

        if (benchmark) {
            runBenchmark();
            return;
        }

        String benchmarkReportPath = getArgValue(args, "--benchmark-report");
        if (benchmarkReportPath != null) {
            try {
                BenchmarkReportGenerator.generateReport(benchmarkReportPath);
                System.out.println("Benchmark report written to: " + benchmarkReportPath);
            } catch (Exception e) {
                System.err.println("Error generating benchmark report: " + e.getMessage());
            }
            return;
        }

        // Normal mode
        List<Task> tasks;

        if (inputPath != null) {
            tasks = CsvLoader.loadFromFile(inputPath);
        } else if (generateStr != null) {
            int count = Integer.parseInt(generateStr);
            tasks = generateRandomTasks(count);
        } else {
            printUsage();
            return;
        }

        if (tasks.isEmpty()) {
            System.out.println("No tasks to schedule.");
            return;
        }

        String schedulerType = (schedulerStr != null) ? schedulerStr : "heap";

        List<Task> scheduled;
        if ("linear".equalsIgnoreCase(schedulerType)) {
            LinearScheduler scheduler = new LinearScheduler();
            for (Task task : tasks) {
                scheduler.addTask(task);
            }
            scheduled = scheduler.getAllScheduled();
        } else {
            HeapScheduler scheduler = new HeapScheduler();
            for (Task task : tasks) {
                scheduler.addTask(task);
            }
            scheduled = scheduler.getAllScheduled();
        }

        System.out.println("Scheduler: " + schedulerType.toUpperCase());
        System.out.println("Scheduled tasks (" + scheduled.size() + " tasks):");
        System.out.println("-".repeat(70));
        for (Task task : scheduled) {
            System.out.printf("%d | %-20s | Priority: %6.2f | Deadline: %s%n",
                    task.getId(),
                    task.getName(),
                    task.getPriority(),
                    task.getDeadline().format(FORMATTER));
        }
    }

    private static void runBenchmark() {
        int taskCount = 500;
        List<Task> tasks = generateRandomTasks(taskCount);

        // Warm up
        for (int i = 0; i < 3; i++) {
            runHeapScheduler(new ArrayList<>(tasks));
            runLinearScheduler(new ArrayList<>(tasks));
        }

        // Benchmark heap scheduler
        long heapTotalTime = 0;
        for (int i = 0; i < 5; i++) {
            List<Task> taskCopy = new ArrayList<>(tasks);
            long start = System.nanoTime();
            runHeapScheduler(taskCopy);
            long end = System.nanoTime();
            heapTotalTime += (end - start);
        }

        // Benchmark linear scheduler
        long linearTotalTime = 0;
        for (int i = 0; i < 5; i++) {
            List<Task> taskCopy = new ArrayList<>(tasks);
            long start = System.nanoTime();
            runLinearScheduler(taskCopy);
            long end = System.nanoTime();
            linearTotalTime += (end - start);
        }

        double heapAvgMs = (heapTotalTime / 5.0) / 1_000_000.0;
        double linearAvgMs = (linearTotalTime / 5.0) / 1_000_000.0;
        double speedup = linearAvgMs / heapAvgMs;

        System.out.println("BENCHMARK RESULTS (" + taskCount + " tasks, 5 iterations)");
        System.out.println("=".repeat(70));
        System.out.printf("Heap Scheduler:   %12.4f ms (avg)%n", heapAvgMs);
        System.out.printf("Linear Scheduler: %12.4f ms (avg)%n", linearAvgMs);
        System.out.println("-".repeat(70));
        System.out.printf("Speedup (linear/heap): %.4fx%n", speedup);
        if (speedup > 1.0) {
            System.out.println("Result: Heap is " + String.format("%.2f", speedup) + "x faster");
        } else {
            System.out.println("Result: Linear is " + String.format("%.2f", 1.0/speedup) + "x faster");
        }
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

    private static boolean containsArg(String[] args, String arg) {
        for (String a : args) {
            if (a.equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }

    private static String getArgValue(String[] args, String arg) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equalsIgnoreCase(arg)) {
                return args[i + 1];
            }
        }
        return null;
    }

    private static void printUsage() {
        System.out.println("Usage: java com.scheduling.Main [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --input <csv-path>    Load tasks from CSV file");
        System.out.println("  --generate <count>    Generate random tasks");
        System.out.println("  --scheduler <type>    Choose scheduler: heap or linear (default: heap)");
        System.out.println("  --benchmark           Run benchmark mode (500 tasks)");
        System.out.println("  --benchmark-report <path>  Generate detailed benchmark report to file");
        System.out.println("  --help                Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java com.scheduling.Main --input tasks.csv");
        System.out.println("  java com.scheduling.Main --generate 100");
        System.out.println("  java com.scheduling.Main --generate 50 --scheduler linear");
        System.out.println("  java com.scheduling.Main --benchmark");
        System.out.println("  java com.scheduling.Main --benchmark-report results.txt");
    }
}
