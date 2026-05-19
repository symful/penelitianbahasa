# Dynamic Priority Scheduling System

A high-performance task scheduling system that compares heap-based (PriorityQueue) and linear (ArrayList) approaches for dynamic priority-based task management. Includes JMH microbenchmarking support.

## Features

- **Dynamic Priority Scheduling** - Calculates task priority based on 4 criteria:
  - Number of LKS (Learning Achievement Checklist)
  - Difficulty level
  - Deadline urgency (days until deadline)
  - Estimated completion time

- **Two Scheduler Implementations**:
  - `HeapScheduler` - Uses Java PriorityQueue with O(log n) insert/poll
  - `LinearScheduler` - Uses ArrayList with O(n) poll for comparison

- **JMH Microbenchmarking** - Production-ready benchmarks using OpenJDK JMH

- **Data Loading** - CSV task loading with validation

- **Random Task Generation** - Built-in generator for testing and benchmarking

## Priority Formula

```
priority = w1*LKS + w2*Difficulty + w3*Urgency + w4*(estimatedMinutes/60)

where:
- Urgency = 1 / (daysUntilDeadline + 1)
- Default weights: w1=w2=w3=w4=1.0
```

## Requirements

- Java 17+
- Maven 3.6+

## Building

```bash
mvn clean compile
mvn test
mvn package -DskipTests
```

## Usage

```bash
# Show help
java -jar target/dynamic-priority-scheduling-1.0.0.jar --help

# Load tasks from CSV
java -jar target/dynamic-priority-scheduling-1.0.0.jar --input src/main/resources/sample_tasks.csv

# Generate random tasks
java -jar target/dynamic-priority-scheduling-1.0.0.jar --generate 100

# Choose scheduler (heap or linear)
java -jar target/dynamic-priority-scheduling-1.0.0.jar --generate 50 --scheduler linear

# Run benchmark (console output)
java -jar target/dynamic-priority-scheduling-1.0.0.jar --benchmark

# Benchmark with report file
java -jar target/dynamic-priority-scheduling-1.0.0.jar --benchmark-report results.txt
```

## CSV Format

```csv
id,name,studentId,lksCount,difficulty,deadline,estimatedMinutes
1,Tugas 1,S001,5,3,2026-05-25T14:30,120
```

## Project Structure

```
src/main/java/com/scheduling/
├── model/Task.java              # Task entity with priority calculation
├── scheduler/
│   ├── HeapScheduler.java      # O(log n) PriorityQueue-based scheduler
│   └── LinearScheduler.java    # O(n) ArrayList-based scheduler
├── util/
│   ├── CsvLoader.java          # CSV file loading
│   ├── PriorityCalculator.java # Priority formula implementation
│   ├── DataGenerator.java      # Random task generation
│   └── BenchmarkReportGenerator.java
├── benchmark/SchedulingBenchmark.java  # JMH benchmarks
└── Main.java                   # CLI entry point
```

## Complexity Analysis

| Operation | HeapScheduler | LinearScheduler |
|-----------|---------------|------------------|
| insert    | O(log n)      | O(1)             |
| poll      | O(log n)      | O(n)             |

## Benchmark Results

See `BENCHMARK_REPORT.txt` for detailed benchmark results comparing heap vs linear scheduling performance.

## License

Educational Use