package com.scheduling.util;

import com.scheduling.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriorityCalculatorTest {

    private Task task;
    private double[] defaultWeights;

    @BeforeEach
    void setUp() {
        task = new Task(1, "Test Task", "S001", 5, 3,
                LocalDateTime.now().plusDays(2), 120);
        defaultWeights = new double[]{1.0, 1.0, 1.0, 1.0};
    }

    @Test
    void calculatePriority_WithDefaultWeights_ReturnsValidDouble() {
        double priority = PriorityCalculator.calculatePriority(task);
        assertNotNull(priority);
        assertTrue(priority >= 0, "Priority should be non-negative");
    }

    @Test
    void calculatePriority_WithNullTask_ReturnsZero() {
        double priority = PriorityCalculator.calculatePriority(null);
        assertEquals(0.0, priority, "Priority of null task should be 0.0");
    }

    @Test
    void calculatePriority_WithPastDeadline_CapsUrgencyAtMaxValue() {
        // Create task with past deadline
        Task pastTask = new Task(2, "Past Task", "S002", 1, 1,
                LocalDateTime.now().minusDays(1), 60);
        double priority = PriorityCalculator.calculatePriority(pastTask);
        // Urgency should be capped at URGENCY_CAP (10.0)
        assertTrue(priority >= 10.0, "Priority with past deadline should have max urgency");
    }

    @Test
    void calculatePriority_WithZeroValues_HandlesGracefully() {
        Task zeroTask = new Task(3, "Zero Task", "S003", 0, 0,
                LocalDateTime.now().plusDays(10), 0);
        double priority = PriorityCalculator.calculatePriority(zeroTask);
        assertNotNull(priority);
        assertTrue(priority >= 0, "Priority should be non-negative even with zero values");
    }

    @Test
    void calculatePriority_WithCustomWeights_AppliesWeightsCorrectly() {
        double[] customWeights = new double[]{2.0, 3.0, 1.0, 1.0};
        double priority = PriorityCalculator.calculatePriority(task, customWeights);
        assertNotNull(priority);
        assertTrue(priority > 0, "Priority should be positive with custom weights");
    }

    @Test
    void calculatePriority_WithNullWeights_ReturnsZero() {
        double priority = PriorityCalculator.calculatePriority(task, null);
        assertEquals(0.0, priority, "Priority with null weights should be 0.0");
    }

    @Test
    void calculatePriority_WithInsufficientWeights_ReturnsZero() {
        double[] insufficientWeights = new double[]{1.0, 2.0}; // Only 2 weights, need 4
        double priority = PriorityCalculator.calculatePriority(task, insufficientWeights);
        assertEquals(0.0, priority, "Priority with insufficient weights should be 0.0");
    }
}
