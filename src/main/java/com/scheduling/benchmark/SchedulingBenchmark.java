package com.scheduling.benchmark;

import com.scheduling.model.Task;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * JMH benchmark for comparing HeapScheduler vs LinearScheduler performance.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class SchedulingBenchmark {

    private List<Task> randomTasks10;
    private List<Task> randomTasks50;
    private List<Task> randomTasks100;
    private List<Task> randomTasks500;

    private PriorityQueue<Task> heapQueue;
    private ArrayList<Task> linearList;

    @Setup
    public void setup() {
        Random random = new Random(42);
        randomTasks10 = generateRandomTasks(10, random);
        randomTasks50 = generateRandomTasks(50, random);
        randomTasks100 = generateRandomTasks(100, random);
        randomTasks500 = generateRandomTasks(500, random);
    }

    private List<Task> generateRandomTasks(int count, Random random) {
        List<Task> tasks = new ArrayList<>(count);
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

    @Benchmark
    public void heapInsert10(Blackhole bh) {
        heapQueue = new PriorityQueue<>();
        for (Task task : randomTasks10) {
            heapQueue.add(task);
        }
        bh.consumeCPU(heapQueue.size());
    }

    @Benchmark
    public void heapPoll10(Blackhole bh) {
        heapQueue = new PriorityQueue<>(randomTasks10);
        while (!heapQueue.isEmpty()) {
            bh.consumeCPU(1);
            heapQueue.poll();
        }
    }

    @Benchmark
    public void heapInsert50(Blackhole bh) {
        heapQueue = new PriorityQueue<>();
        for (Task task : randomTasks50) {
            heapQueue.add(task);
        }
        bh.consumeCPU(heapQueue.size());
    }

    @Benchmark
    public void heapPoll50(Blackhole bh) {
        heapQueue = new PriorityQueue<>(randomTasks50);
        while (!heapQueue.isEmpty()) {
            bh.consumeCPU(1);
            heapQueue.poll();
        }
    }

    @Benchmark
    public void heapInsert100(Blackhole bh) {
        heapQueue = new PriorityQueue<>();
        for (Task task : randomTasks100) {
            heapQueue.add(task);
        }
        bh.consumeCPU(heapQueue.size());
    }

    @Benchmark
    public void heapPoll100(Blackhole bh) {
        heapQueue = new PriorityQueue<>(randomTasks100);
        while (!heapQueue.isEmpty()) {
            bh.consumeCPU(1);
            heapQueue.poll();
        }
    }

    @Benchmark
    public void heapInsert500(Blackhole bh) {
        heapQueue = new PriorityQueue<>();
        for (Task task : randomTasks500) {
            heapQueue.add(task);
        }
        bh.consumeCPU(heapQueue.size());
    }

    @Benchmark
    public void heapPoll500(Blackhole bh) {
        heapQueue = new PriorityQueue<>(randomTasks500);
        while (!heapQueue.isEmpty()) {
            bh.consumeCPU(1);
            heapQueue.poll();
        }
    }

    @Benchmark
    public void linearInsert10(Blackhole bh) {
        linearList = new ArrayList<>();
        for (Task task : randomTasks10) {
            linearList.add(task);
        }
        bh.consumeCPU(linearList.size());
    }

    @Benchmark
    public void linearPoll10(Blackhole bh) {
        linearList = new ArrayList<>(randomTasks10);
        while (!linearList.isEmpty()) {
            int maxIndex = 0;
            for (int i = 1; i < linearList.size(); i++) {
                if (linearList.get(i).compareTo(linearList.get(maxIndex)) < 0) {
                    maxIndex = i;
                }
            }
            bh.consumeCPU(1);
            linearList.remove(maxIndex);
        }
    }

    @Benchmark
    public void linearInsert50(Blackhole bh) {
        linearList = new ArrayList<>();
        for (Task task : randomTasks50) {
            linearList.add(task);
        }
        bh.consumeCPU(linearList.size());
    }

    @Benchmark
    public void linearPoll50(Blackhole bh) {
        linearList = new ArrayList<>(randomTasks50);
        while (!linearList.isEmpty()) {
            int maxIndex = 0;
            for (int i = 1; i < linearList.size(); i++) {
                if (linearList.get(i).compareTo(linearList.get(maxIndex)) < 0) {
                    maxIndex = i;
                }
            }
            bh.consumeCPU(1);
            linearList.remove(maxIndex);
        }
    }

    @Benchmark
    public void linearInsert100(Blackhole bh) {
        linearList = new ArrayList<>();
        for (Task task : randomTasks100) {
            linearList.add(task);
        }
        bh.consumeCPU(linearList.size());
    }

    @Benchmark
    public void linearPoll100(Blackhole bh) {
        linearList = new ArrayList<>(randomTasks100);
        while (!linearList.isEmpty()) {
            int maxIndex = 0;
            for (int i = 1; i < linearList.size(); i++) {
                if (linearList.get(i).compareTo(linearList.get(maxIndex)) < 0) {
                    maxIndex = i;
                }
            }
            bh.consumeCPU(1);
            linearList.remove(maxIndex);
        }
    }

    @Benchmark
    public void linearInsert500(Blackhole bh) {
        linearList = new ArrayList<>();
        for (Task task : randomTasks500) {
            linearList.add(task);
        }
        bh.consumeCPU(linearList.size());
    }

    @Benchmark
    public void linearPoll500(Blackhole bh) {
        linearList = new ArrayList<>(randomTasks500);
        while (!linearList.isEmpty()) {
            int maxIndex = 0;
            for (int i = 1; i < linearList.size(); i++) {
                if (linearList.get(i).compareTo(linearList.get(maxIndex)) < 0) {
                    maxIndex = i;
                }
            }
            bh.consumeCPU(1);
            linearList.remove(maxIndex);
        }
    }
}