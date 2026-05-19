# Plan: Dynamic Priority Scheduling System

## TL;DR

> **Quick Summary**: Sistem penjadwalan tugas mahasiswa menggunakan Dynamic Priority Scheduling dengan Priority Queue (binary heap) dalam Java. CLI dengan JMH benchmark untuk membandingkan O(log n) vs O(n).
>
> **Deliverables**:
> - Maven project dengan struktur `com.scheduling`
> - Task class dengan 4 variabel prioritas
> - Heap implementation (PriorityQueue) dan Linear implementation (ArrayList scan)
> - JMH benchmark untuk perbandingan performa
> - CSV file loader
> - CLI untuk run dan output hasil
>
> **Estimated Effort**: Medium
> **Parallel Execution**: YES - 3 waves
> **Critical Path**: Project setup → Task class → Priority implementations → Benchmark → Validation

---

## Context

### Original Request
Penelitian implementasi Dynamic Priority Scheduling menggunakan Priority Queue untuk manajemen tugas mahasiswa. Bahasa Java, CLI, load from file, JMH benchmark.

### Interview Summary
**Key Discussions**:
- Interface: CLI (Java console)
- Input: Load from file (CSV)
- Benchmark: JMH (Java Microbenchmark Harness)
- Performance targets: ≥95% accuracy, <0.1s response untuk 500 tasks

**Metis Review Findings**:
- Greenfield project - perlu buat dari scratch
- CSV format perlu confirmation (columns, header)
- Weight values perlu decision
- Accuracy definition perlu clarification

---

## Work Objectives

### Core Objective
Sistem penjadwalan yang menentukan prioritas tugas secara dinamis berdasarkan LKS, difficulty, deadline, dan estimated time, dengan perbandingan heap vs linear.

### Concrete Deliverables
- [x] `pom.xml` dengan JMH dependency
- [x] `src/main/java/com/scheduling/model/Task.java`
- [x] `src/main/java/com/scheduling/scheduler/HeapScheduler.java`
- [x] `src/main/java/com/scheduling/scheduler/LinearScheduler.java`
- [x] `src/main/java/com/scheduling/benchmark/SchedulingBenchmark.java`
- [x] `src/main/java/com/scheduling/util/CsvLoader.java`
- [x] `src/main/java/com/scheduling/Main.java`
- [x] `src/main/resources/sample_tasks.csv`
- [x] `src/main/java/com/scheduling/util/PriorityCalculator.java`

### Definition of Done
- [ ] `mvn compile` → BUILD SUCCESS
- [ ] `mvn test` → All tests pass
- [ ] Benchmark runs dan output latency
- [ ] CLI menampilkan ordered task list
- [ ] Akurasi ≥95% untuk heap ordering

### Must Have
- PriorityQueue (heap) dengan O(log n) insert/poll
- ArrayList linear scan dengan O(n) untuk comparison
- JMH benchmark dengan warmup
- CSV loader
- CLI interface
- 4 kriteria prioritas dengan configurable weights

### Must NOT Have (Guardrails)
- NO GUI (Swing, JavaFX)
- NO web interface
- NO database/persistence
- NO multithreading
- NO external heap libraries (hanya java.util.PriorityQueue)
- NO priority aging/recalculation while in queue
- NO visualization/charts

---

## Verification Strategy

### Test Decision
- **Infrastructure exists**: YES (Maven)
- **Automated tests**: YES (JUnit 5)
- **Framework**: Maven with JMH
- **TDD**: Tests-after (JUnit for unit tests)

### QA Policy
Every task includes agent-executed QA scenarios. Evidence saved to `.sisyphus/evidence/`.

- **Compilation**: `mvn compile -q`
- **Unit tests**: `mvn test -q`
- **Benchmark**: `mvn jmh:benchmark -Djmh.includes=.*SchedulingBenchmark.*`
- **CLI verification**: Run with sample CSV, verify output ordering

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Foundation - scaffolding):
├── Task 1: Project setup (pom.xml, directory structure)
├── Task 2: Task model class
├── Task 3: PriorityCalculator utility
└── Task 4: CSV loader utility

Wave 2 (Core implementations):
├── Task 5: HeapScheduler (PriorityQueue)
├── Task 6: LinearScheduler (ArrayList scan)
└── Task 7: JMH benchmark setup

Wave 3 (Integration + Validation):
├── Task 8: Main CLI integration
├── Task 9: Sample data generation
└── Task 10: Unit tests (Task, PriorityCalculator, Schedulers)
```

### Dependency Matrix

- **1**: - - 5, 6, 7, 8, 9, 10, 2
- **2**: 1 - 5, 6, 7, 8, 9, 10, 3
- **3**: 1 - 5, 6, 7, 8, 10, 4
- **4**: 1 - 8
- **5**: 2, 3 - 8
- **6**: 2, 3 - 8
- **7**: 2, 3 - (standalone)
- **8**: 4, 5, 6 - 9
- **9**: 8 - 10
- **10**: 5, 6, 8 - (final)

---

## TODOs

> Implementation + Test = ONE Task. Every task MUST have: Recommended Agent Profile + QA Scenarios.
> **A task WITHOUT QA Scenarios is INCOMPLETE.**

- [x] 1. Project Setup (pom.xml, directory structure)

  **What to do**:
  - Create Maven project structure
  - Add dependencies: JMH, OpenCSV, JUnit 5
  - Configure maven-compiler-plugin (Java 17)
  - Create package structure: com.scheduling.model, scheduler, util, benchmark

  **Must NOT do**:
  - Add Spring/boot dependencies
  - Add database drivers

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: []
  - **Reason**: Simple project scaffolding, standard Maven setup

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 2, 3, 4)
  - **Blocks**: Tasks 5, 6, 7, 8, 9, 10
  - **Blocked By**: None (can start immediately)

  **References**:
  - JMH Maven setup: `https://openjdk.org/projects/code-tools/jmh/` - JMH maven plugin configuration
  - OpenCSV: `com.opencsv:opencsv:5.9` - CSV parsing library

  **Acceptance Criteria**:
  - [ ] pom.xml exists with correct dependencies
  - [ ] Directory structure created
  - [ ] `mvn compile -q` → BUILD SUCCESS

  **QA Scenarios**:
  ```
  Scenario: Maven project compiles successfully
    Tool: Bash
    Preconditions: Clean directory, no existing pom.xml
    Steps:
      1. Run `mvn compile -q`
    Expected Result: BUILD SUCCESS with no errors
    Failure Indicators: "Cannot resolve symbol" errors, missing dependencies
    Evidence: .sisyphus/evidence/task-1-compile.txt

  Scenario: Directory structure is correct
    Tool: Bash
    Preconditions: Maven project created
    Steps:
      1. Run `find src -type d | sort`
    Expected Result: Shows com/scheduling/{model,scheduler,util,benchmark} directories
    Evidence: .sisyphus/evidence/task-1-structure.txt
  ```

  **Commit**: YES
  - Message: `chore: initial maven project structure`
  - Files: pom.xml, directory structure

---

- [x] 2. Task Model Class

  **What to do**:
  - Create `Task.java` with fields: id, name, studentId, lksCount, difficulty (1-5), deadline (LocalDateTime), estimatedMinutes
  - Add `getPriority()` method using PriorityCalculator
  - Add getters, setters, toString
  - Implement Comparable<Task> for heap consistency

  **Must NOT do**:
  - Add task status (completed/pending) - focus only on scheduling
  - Add mutable priority field after construction

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: []
  - **Reason**: Simple POJO with standard Java patterns

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 3, 4)
  - **Blocks**: Tasks 5, 6
  - **Blocked By**: Task 1

  **References**:
  - `java.time.LocalDateTime` - deadline storage
  - `java.util.Comparator` - for PriorityQueue ordering

  **Acceptance Criteria**:
  - [ ] Task class has all 6 fields
  - [ ] getPriority() returns double
  - [ ] Comparable implemented
  - [ ] toString() readable

  **QA Scenarios**:
  ```
  Scenario: Task object creation with all fields
    Tool: Bash (jshell)
    Preconditions: Task.java compiled
    Steps:
      1. Run `jshell --class-path target/classes`
      2. Enter: `new com.scheduling.model.Task(1, "Tugas 1", "S001", 5, 3, java.time.LocalDateTime.now().plusDays(3), 120)`
      3. Enter: `/exit`
    Expected Result: Task object created successfully, no exceptions
    Evidence: .sisyphus/evidence/task-2-task-creation.txt

  Scenario: Task priority calculation
    Tool: Bash
    Preconditions: Task compiled
    Steps:
      1. Run `java -cp target/classes com.scheduling.model.TaskTest` (if exists)
    Expected Result: Priority calculated correctly
    Evidence: .sisyphus/evidence/task-2-priority.txt
  ```

  **Commit**: YES
  - Message: `feat: add Task model class`
  - Files: src/main/java/com/scheduling/model/Task.java

---

- [x] 3. PriorityCalculator Utility

  **What to do**:
  - Create `PriorityCalculator.java` with static method `calculatePriority(Task task, double[] weights)`
  - Formula: `priority = w1*lks + w2*difficulty + w3*urgency + w4*estimatedMinutes/60`
  - Urgency: `1.0 / (daysUntilDeadline + 1)` (add 1 to avoid division by zero)
  - Default weights: [1.0, 1.0, 1.0, 1.0]
  - Provide overloaded method with default weights

  **Must NOT do**:
  - Store weights in static state
  - Modify task inside calculation

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: []
  - **Reason**: Pure utility class, straightforward math

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 2, 4)
  - **Blocks**: Tasks 5, 6, 10
  - **Blocked By**: Task 1

  **References**:
  - `java.time.temporal.ChronoUnit` - for days calculation
  - Double precision for weights

  **Acceptance Criteria**:
  - [ ] calculatePriority returns double between 0 and ~15 (based on formula)
  - [ ] Handles deadline in past (cap urgency at max)
  - [ ] Handles zero values gracefully

  **QA Scenarios**:
  ```
  Scenario: Priority calculation with default weights
    Tool: Bash
    Preconditions: PriorityCalculator compiled
    Steps:
      1. Create test task with: lks=5, difficulty=3, deadline=today+3days, time=120min
      2. Call calculatePriority(task)
    Expected Result: priority > 0, no NaN or Infinity
    Evidence: .sisyphus/evidence/task-3-default.txt

  Scenario: Urgency caps for past deadline
    Tool: Bash
    Preconditions: PriorityCalculator compiled
    Steps:
      1. Create task with deadline = yesterday
      2. Call calculatePriority(task)
    Expected Result: Urgency capped at reasonable max value
    Evidence: .sisyphus/evidence/task-3-urgency.txt
  ```

  **Commit**: YES
  - Message: `feat: add PriorityCalculator utility`
  - Files: src/main/java/com/scheduling/util/PriorityCalculator.java

---

- [x] 4. CSV Loader Utility

  **What to do**:
  - Create `CsvLoader.java` using OpenCSV
  - Method: `List<Task> loadFromFile(String path)`
  - Expected CSV format: `id,name,studentId,lksCount,difficulty,deadline,estimatedMinutes`
  - Header row assumed present
  - Parse deadline as ISO LocalDateTime
  - Skip malformed rows (log warning)

  **Must NOT do**:
  - Assume specific encoding (use UTF-8)
  - Validate task business rules (just parse)

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: []
  - **Reason**: Standard file parsing, no complex logic

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 2, 3)
  - **Blocks**: Task 8
  - **Blocked By**: Task 1

  **References**:
  - OpenCSV `CSVReaderBuilder` with `withHeader()` - for CSV parsing

  **Acceptance Criteria**:
  - [ ] Load valid CSV and return List<Task>
  - [ ] Skip malformed rows (don't crash)
  - [ ] Empty file returns empty list

  **QA Scenarios**:
  ```
  Scenario: Load valid CSV file
    Tool: Bash
    Preconditions: CsvLoader compiled, sample_tasks.csv exists
    Steps:
      1. Run `java -cp target/classes com.scheduling.util.CsvLoader src/main/resources/sample_tasks.csv`
    Expected Result: Returns list of tasks
    Evidence: .sisyphus/evidence/task-4-valid-csv.txt

  Scenario: Handle empty CSV
    Tool: Bash
    Preconditions: CsvLoader compiled, empty.csv exists
    Steps:
      1. Run with empty file
    Expected Result: Returns empty list, no crash
    Evidence: .sisyphus/evidence/task-4-empty.txt

  Scenario: Skip malformed rows
    Tool: Bash
    Preconditions: CsvLoader compiled, malformed.csv exists
    Steps:
      1. Run with missing fields
    Expected Result: Returns valid tasks, skips bad rows
    Evidence: .sisyphus/evidence/task-4-malformed.txt
  ```

  **Commit**: YES
  - Message: `feat: add CSV loader utility`
  - Files: src/main/java/com/scheduling/util/CsvLoader.java

---

- [x] 5. HeapScheduler (PriorityQueue)

  **What to do**:
  - Create `HeapScheduler.java`
  - Uses `PriorityQueue<Task>` with Comparator comparing by priority (reversed for max-heap)
  - Tie-breaker: earlier deadline wins, then by ID
  - Methods:
    - `void addTask(Task task)` - O(log n)
    - `Task pollNextTask()` - O(log n)
    - `List<Task> getAllScheduled()` - returns all in priority order
  - Extend or implement Scheduler interface

  **Must NOT do**:
  - Use any heap implementation other than PriorityQueue
  - Modify Task after adding to queue

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: []
  - **Reason**: Standard PriorityQueue usage, well-documented pattern

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 2 (with Task 6, 7)
  - **Blocks**: Task 8
  - **Blocked By**: Tasks 2, 3

  **References**:
  - `java.util.PriorityQueue` - Oracle docs for heap implementation
  - `Comparator.comparingDouble(Task::getPriority).reversed()` - max-heap pattern

  **Acceptance Criteria**:
  - [ ] addTask O(log n)
  - [ ] pollNextTask O(log n)
  - [ ] Tasks returned in priority order (highest first)
  - [ ] Tie-breaker works (earlier deadline first)
  - [ ] getAllScheduled returns all tasks sorted

  **QA Scenarios**:
  ```
  Scenario: Schedule tasks and retrieve in priority order
    Tool: Bash (jshell)
    Preconditions: HeapScheduler compiled
    Steps:
      1. Create 3 tasks with different priorities
      2. Add all to scheduler
      3. Poll 3 times
    Expected Result: Tasks returned highest priority first, no nulls, no duplicates
    Evidence: .sisyphus/evidence/task-5-order.txt

  Scenario: Tie-breaker resolves equal priorities
    Tool: Bash (jshell)
    Preconditions: HeapScheduler compiled
    Steps:
      1. Create 2 tasks with identical priority but different deadlines
      2. Add both, poll twice
    Expected Result: Earlier deadline first
    Evidence: .sisyphus/evidence/task-5-tiebreaker.txt

  Scenario: Performance - 500 tasks
    Tool: Bash
    Preconditions: HeapScheduler compiled
    Steps:
      1. Add 500 tasks, poll all
    Expected Result: Completes without OOM or crash
    Evidence: .sisyphus/evidence/task-5-perf.txt
  ```

  **Commit**: YES
  - Message: `feat: implement HeapScheduler with PriorityQueue`
  - Files: src/main/java/com/scheduling/scheduler/HeapScheduler.java

---

- [x] 6. LinearScheduler (ArrayList scan)

  **What to do**:
  - Create `LinearScheduler.java`
  - Uses `ArrayList<Task>` with linear scan for max priority
  - Methods:
    - `void addTask(Task task)` - O(1) append
    - `Task pollNextTask()` - O(n) linear scan for max, then remove
    - `List<Task> getAllScheduled()` - returns all sorted
  - Must use same Task objects and Comparator as HeapScheduler

  **Must NOT do**:
  - Sort the list (must use linear scan for poll)
  - Use different priority calculation

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: []
  - **Reason**: Simple ArrayList operations, straightforward

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 2 (with Task 5, 7)
  - **Blocks**: Task 8
  - **Blocked By**: Tasks 2, 3

  **References**:
  - `java.util.stream.StreamSupport` - for linear max search
  - Uses same Comparator as HeapScheduler

  **Acceptance Criteria**:
  - [ ] addTask O(1)
  - [ ] pollNextTask O(n)
  - [ ] Same ordering as HeapScheduler for identical inputs
  - [ ] getAllScheduled returns all tasks sorted

  **QA Scenarios**:
  ```
  Scenario: Linear schedule produces same order as heap
    Tool: Bash
    Preconditions: Both schedulers compiled
    Steps:
      1. Create identical task list
      2. Add to both schedulers
      3. Compare poll order
    Expected Result: Both return tasks in identical priority order
    Evidence: .sisyphus/evidence/task-6-same-order.txt

  Scenario: 500 tasks completes without crash
    Tool: Bash
    Preconditions: LinearScheduler compiled
    Steps:
      1. Add 500 tasks, poll all
    Expected Result: Completes, slower than heap but works
    Evidence: .sisyphus/evidence/task-6-perf.txt
  ```

  **Commit**: YES
  - Message: `feat: implement LinearScheduler for comparison`
  - Files: src/main/java/com/scheduling/scheduler/LinearScheduler.java

---

- [x] 7. JMH Benchmark Setup

  **What to do**:
  - Create `SchedulingBenchmark.java`
  - Annotations: `@State(Scope.Thread)`, `@BenchmarkMode(Mode.AverageTime)`
  - Parameters: taskCount = {10, 50, 100, 500}
  - Warmup: 10 iterations, 3 forks
  - Benchmark methods:
    - `heapInsert()` - measure PriorityQueue.add()
    - `heapPoll()` - measure PriorityQueue.poll()
    - `linearInsert()` - measure ArrayList.add()
    - `linearPoll()` - measure ArrayList linear scan + remove
  - Results in ns/op

  **Must NOT do**:
  - Include file I/O in benchmark (measure only scheduling ops)
  - Run without warmup

  **Recommended Agent Profile**:
  - **Category**: `deep`
  - **Skills**: []
  - **Reason**: JMH requires correct annotation and state management

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 2 (with Task 5, 6)
  - **Blocks**: None (standalone benchmark)
  - **Blocked By**: Tasks 2, 3

  **References**:
  - JMH documentation for benchmark patterns
  - `jmh-maven-plugin` configuration

  **Acceptance Criteria**:
  - [ ] Benchmark runs with JMH
  - [ ] Warmup completes before measurement
  - [ ] Results show ns/op for each operation
  - [ ] Different task counts (10, 50, 100, 500) tested

  **QA Scenarios**:
  ```
  Scenario: JMH benchmark runs successfully
    Tool: Bash
    Preconditions: Benchmark compiled, JMH plugin configured
    Steps:
      1. Run `mvn jmh:benchmark -Djmh.includes=.*SchedulingBenchmark.* -DjmhIterations=3`
    Expected Result: Benchmark completes, outputs latency numbers
    Evidence: .sisyphus/evidence/task-7-benchmark.txt

  Scenario: Benchmark shows O(log n) vs O(n) difference
    Tool: Bash
    Preconditions: Benchmark results available
    Steps:
      1. Compare heap vs linear for n=500
    Expected Result: Heap significantly faster for poll()
    Evidence: .sisyphus/evidence/task-7-comparison.txt
  ```

  **Commit**: YES
  - Message: `feat: add JMH benchmark for heap vs linear`
  - Files: src/main/java/com/scheduling/benchmark/SchedulingBenchmark.java

---

- [x] 8. Main CLI Integration

  **What to do**:
  - Create `Main.java` with CLI interface
  - Arguments: `--input <csv-path>` or `--generate <count>`
  - Options: `--scheduler heap|linear`, `--benchmark`
  - Output: ordered task list with priority scores
  - Default: load from file, use heap, print results

  **Must NOT do**:
  - Add interactive prompts (batch mode only)
  - Add color/formatting (plain text only)

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: []
  - **Reason**: Simple CLI argument parsing, standard pattern

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (with Task 9, 10)
  - **Blocks**: Task 9
  - **Blocked By**: Tasks 4, 5, 6

  **References**:
  - `picocli` or manual argument parsing (no external library needed)
  - `java.util.List` output formatting

  **Acceptance Criteria**:
  - [ ] `java -jar target/*.jar --input sample.csv` works
  - [ ] `--generate 100` creates random tasks
  - [ ] Output shows task list in priority order

  **QA Scenarios**:
  ```
  Scenario: Load CSV and display ordered tasks
    Tool: Bash
    Preconditions: JAR built, sample_tasks.csv exists
    Steps:
      1. Run `java -jar target/*.jar --input src/main/resources/sample_tasks.csv`
    Expected Result: Displays tasks ordered by priority
    Evidence: .sisyphus/evidence/task-8-load.txt

  Scenario: Generate random tasks
    Tool: Bash
    Preconditions: JAR built
    Steps:
      1. Run `java -jar target/*.jar --generate 50 --scheduler linear`
    Expected Result: Creates 50 random tasks, schedules with linear, prints order
    Evidence: .sisyphus/evidence/task-8-generate.txt

  Scenario: Benchmark mode
    Tool: Bash
    Preconditions: JAR built
    Steps:
      1. Run `java -jar target/*.jar --benchmark --generate 500`
    Expected Result: Runs benchmark, shows performance comparison
    Evidence: .sisyphus/evidence/task-8-benchmark.txt
  ```

  **Commit**: YES
  - Message: `feat: add Main CLI entry point`
  - Files: src/main/java/com/scheduling/Main.java

---

- [x] 9. Sample Data Generation

  **What to do**:
  - Create `src/main/resources/sample_tasks.csv` with 30 tasks
  - Cover various scenarios: high/low priority, near/far deadline, different difficulties
  - Create `DataGenerator.java` utility for generating random tasks
  - Generate CSV files for benchmark (10, 50, 100, 500 tasks)

  **Must NOT do**:
  - Use real student names (use anonymized IDs like S001, S002)
  - Generate unrealistic deadlines (use future dates only)

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: []
  - **Reason**: Data generation script, straightforward

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (with Task 8, 10)
  - **Blocks**: None (data prep)
  - **Blocked By**: Task 8

  **References**:
  - CSV format from CsvLoader spec

  **Acceptance Criteria**:
  - [ ] sample_tasks.csv has 30 rows + header
  - [ ] Each benchmark size has corresponding CSV
  - [ ] Data covers edge cases: equal priorities, past deadlines

  **QA Scenarios**:
  ```
  Scenario: Sample CSV loads correctly
    Tool: Bash
    Preconditions: CSV file exists
    Steps:
      1. Run Main with --input sample_tasks.csv
    Expected Result: 30 tasks loaded and displayed
    Evidence: .sisyphus/evidence/task-9-sample.txt

  Scenario: Generated files for all benchmark sizes
    Tool: Bash
    Preconditions: DataGenerator created
    Steps:
      1. Check files: tasks_10.csv, tasks_50.csv, tasks_100.csv, tasks_500.csv
    Expected Result: All files exist with correct row counts
    Evidence: .sisyphus/evidence/task-9-files.txt
  ```

  **Commit**: YES
  - Message: `feat: add sample data for testing`
  - Files: src/main/resources/sample_tasks.csv, DataGenerator.java

---

- [x] 10. Unit Tests

  **What to do**:
  - Create JUnit 5 tests:
    - `TaskTest.java` - priority calculation, getters, toString
    - `PriorityCalculatorTest.java` - edge cases (past deadline, zero values)
    - `HeapSchedulerTest.java` - ordering, tie-breaker, empty queue
    - `LinearSchedulerTest.java` - ordering consistency with heap
    - `CsvLoaderTest.java` - valid/invalid/missing data
  - Use @BeforeEach for common setup
  - Test edge cases explicitly

  **Must NOT do**:
  - Add integration tests (that's final verification)
  - Mock external resources

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: []
  - **Reason**: Standard JUnit tests, no special complexity

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (with Task 8, 9)
  - **Blocks**: None (final task)
  - **Blocked By**: Tasks 5, 6, 8

  **References**:
  - JUnit 5 assertions: `assertEquals`, `assertTrue`, `assertThrows`
  - `@BeforeEach` setup pattern

  **Acceptance Criteria**:
  - [ ] All tests pass: `mvn test -q`
  - [ ] Each scheduler test includes ordering verification
  - [ ] Edge case tests cover boundary conditions

  **QA Scenarios**:
  ```
  Scenario: All unit tests pass
    Tool: Bash
    Preconditions: Tests created
    Steps:
      1. Run `mvn test -q`
    Expected Result: All tests pass, BUILD SUCCESS
    Evidence: .sisyphus/evidence/task-10-test-results.txt

  Scenario: Verify heap ordering with known inputs
    Tool: Bash
    Preconditions: HeapSchedulerTest passes
    Steps:
      1. Check test output for ordering verification
    Expected Result: Tests confirm correct priority ordering
    Evidence: .sisyphus/evidence/task-10-ordering.txt
  ```

  **Commit**: YES
  - Message: `test: add unit tests for all components`
  - Files: src/test/java/com/scheduling/**/*Test.java

---

## Final Verification Wave

> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.

- [x] F1. **Plan Compliance Audit** — `oracle`

- [x] F2. **Code Quality Review** — `unspecified-high`

- [x] F3. **Real Manual QA** — `unspecified-high`

- [x] F4. **Scope Fidelity Check** — `deep`

---

## Commit Strategy

- **1**: `chore: initial maven project structure` - pom.xml, directories
- **2**: `feat: add Task model class` - Task.java
- **3**: `feat: add PriorityCalculator utility` - PriorityCalculator.java
- **4**: `feat: add CSV loader utility` - CsvLoader.java
- **5**: `feat: implement HeapScheduler with PriorityQueue` - HeapScheduler.java
- **6**: `feat: implement LinearScheduler for comparison` - LinearScheduler.java
- **7**: `feat: add JMH benchmark for heap vs linear` - SchedulingBenchmark.java
- **8**: `feat: add Main CLI entry point` - Main.java
- **9**: `feat: add sample data for testing` - sample_tasks.csv, DataGenerator.java
- **10**: `test: add unit tests for all components` - *Test.java files

---

## Success Criteria

### Verification Commands
```bash
mvn compile -q                    # Expected: BUILD SUCCESS
mvn test -q                       # Expected: All tests pass
mvn jmh:benchmark -Djmh.includes=.*SchedulingBenchmark.*  # Expected: benchmark runs
java -jar target/*.jar --input src/main/resources/sample_tasks.csv  # Expected: ordered list
```

### Final Checklist
- [ ] All "Must Have" present
- [ ] All "Must NOT Have" absent
- [ ] All tests pass
- [ ] Benchmark produces comparison data
- [ ] CLI works as specified

---

## Decisions Needed

> These critical decisions must be answered before plan can proceed:

1. **CSV Format**: What are the exact columns? Is there a header row?
   - Options: Provide sample file, or specify column order

2. **Weight Values**: What are w1, w2, w3, w4 for the priority formula?
   - Options: Equal weights (1.0 each), or specify custom values

3. **Accuracy Definition**: What does "≥95% accuracy" mean specifically?
   - Options: Heap ordering correctness (pairwise comparison), or deadline adherence rate