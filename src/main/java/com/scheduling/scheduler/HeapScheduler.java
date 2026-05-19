package com.scheduling.scheduler;

import com.scheduling.model.Task;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class HeapScheduler {

    private final PriorityQueue<Task> queue;

    public HeapScheduler() {
        // Comparator: priority DESC, deadline ASC, id ASC
        Comparator<Task> comparator = Comparator
                .comparingDouble(Task::getPriority)
                .reversed()
                .thenComparing(Task::getDeadline)
                .thenComparingInt(Task::getId);
        this.queue = new PriorityQueue<>(comparator);
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new NullPointerException("Task cannot be null");
        }
        queue.offer(task);
    }

    public Task pollNextTask() {
        return queue.poll();
    }

    public List<Task> getAllScheduled() {
        // Create a copy of the queue and drain it to get sorted order
        PriorityQueue<Task> copy = new PriorityQueue<>(queue);
        List<Task> result = new ArrayList<>();
        while (!copy.isEmpty()) {
            result.add(copy.poll());
        }
        return result;
    }
}