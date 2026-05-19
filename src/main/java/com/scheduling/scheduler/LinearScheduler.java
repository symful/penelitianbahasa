package com.scheduling.scheduler;

import com.scheduling.model.Task;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LinearScheduler {

    private final ArrayList<Task> tasks;

    public LinearScheduler() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new NullPointerException("Task cannot be null");
        }
        tasks.add(task);
    }

    public Task pollNextTask() {
        if (tasks.isEmpty()) {
            return null;
        }

        // Linear scan to find task with highest priority
        int maxIndex = 0;
        for (int i = 1; i < tasks.size(); i++) {
            if (tasks.get(i).compareTo(tasks.get(maxIndex)) < 0) {
                maxIndex = i;
            }
        }

        return tasks.remove(maxIndex);
    }

    public List<Task> getAllScheduled() {
        List<Task> sorted = new ArrayList<>(tasks);
        sorted.sort(Comparator
                .comparingDouble(Task::getPriority)
                .reversed()
                .thenComparing(Task::getDeadline)
                .thenComparingInt(Task::getId));
        return sorted;
    }
}