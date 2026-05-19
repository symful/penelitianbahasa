package com.scheduling.model;

import com.scheduling.util.PriorityCalculator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Task implements Comparable<Task> {

    private final int id;
    private final String name;
    private final String studentId;
    private final int lksCount;
    private final int difficulty;
    private final LocalDateTime deadline;
    private final int estimatedMinutes;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Task(int id, String name, String studentId, int lksCount, int difficulty,
                LocalDateTime deadline, int estimatedMinutes) {
        this.id = id;
        this.name = name;
        this.studentId = studentId;
        this.lksCount = lksCount;
        this.difficulty = difficulty;
        this.deadline = deadline;
        this.estimatedMinutes = estimatedMinutes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getLksCount() {
        return lksCount;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public double getPriority() {
        return PriorityCalculator.calculatePriority(this);
    }

    @Override
    public int compareTo(Task other) {
        if (other == null) {
            throw new NullPointerException("Task cannot be null");
        }

        // Compare by priority DESC (higher priority first)
        double thisPriority = this.getPriority();
        double otherPriority = other.getPriority();
        if (thisPriority != otherPriority) {
            return Double.compare(otherPriority, thisPriority); // reversed for DESC
        }

        // Tie-breaker 1: deadline ASC (earlier deadline first)
        int deadlineCompare = this.deadline.compareTo(other.deadline);
        if (deadlineCompare != 0) {
            return deadlineCompare;
        }

        // Tie-breaker 2: id ASC (lower id first)
        return Integer.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return String.format("Task[id=%d, name='%s', priority=%.2f, deadline=%s]",
                id, name, getPriority(), deadline.format(FORMATTER));
    }
}
