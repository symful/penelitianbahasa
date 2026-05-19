package com.scheduling.model;

import com.scheduling.util.PriorityCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        // Create tasks with different priorities
        task1 = new Task(1, "Task One", "S001", 5, 3,
                LocalDateTime.now().plusDays(2), 120);
        task2 = new Task(2, "Task Two", "S002", 10, 5,
                LocalDateTime.now().plusDays(1), 60);
        task3 = new Task(3, "Task Three", "S003", 5, 3,
                LocalDateTime.now().plusDays(3), 90);
    }

    @Test
    void getPriority_ReturnsNonNegativeValue() {
        assertTrue(task1.getPriority() >= 0, "Priority should be non-negative");
        assertTrue(task2.getPriority() >= 0, "Priority should be non-negative");
        assertTrue(task3.getPriority() >= 0, "Priority should be non-negative");
    }

    @Test
    void compareTo_HigherPriorityComesFirst() {
        // task2 has higher priority due to more lksCount and difficulty
        assertTrue(task2.compareTo(task1) < 0, "Higher priority task should come first");
    }

    @Test
    void compareTo_TieBreaker_EarlierDeadlineFirst() {
        // Create tasks with same priority but different deadlines
        Task taskA = new Task(10, "Task A", "S010", 5, 3,
                LocalDateTime.now().plusDays(5), 120);
        Task taskB = new Task(11, "Task B", "S011", 5, 3,
                LocalDateTime.now().plusDays(3), 120);

        // Same priority values should result in earlier deadline coming first
        double priorityA = PriorityCalculator.calculatePriority(taskA);
        double priorityB = PriorityCalculator.calculatePriority(taskB);

        // If priorities are equal, earlier deadline should come first
        assertTrue(taskB.compareTo(taskA) < 0, "Earlier deadline should come first when priorities are equal");
    }

    @Test
    void toString_ReturnsReadableString() {
        String result = task1.toString();
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("Task[id="), "toString should contain Task[id=");
        assertTrue(result.contains("name='"), "toString should contain name='");
        assertTrue(result.contains("priority="), "toString should contain priority=");
        assertTrue(result.contains("deadline="), "toString should contain deadline=");
    }

    @Test
    void compareTo_ThrowsNullPointerException_WhenOtherIsNull() {
        assertThrows(NullPointerException.class, () -> task1.compareTo(null));
    }
}
