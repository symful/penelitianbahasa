package com.scheduling.scheduler;

import com.scheduling.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinearSchedulerTest {

    private LinearScheduler scheduler;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        scheduler = new LinearScheduler();
        // Create tasks with known different priorities
        // task2 should have highest priority (more lksCount and difficulty)
        task1 = new Task(1, "Task One", "S001", 3, 2,
                LocalDateTime.now().plusDays(3), 60);
        task2 = new Task(2, "Task Two", "S002", 10, 5,
                LocalDateTime.now().plusDays(1), 120);
        task3 = new Task(3, "Task Three", "S003", 5, 3,
                LocalDateTime.now().plusDays(2), 90);
    }

    @Test
    void addTask_And_PollNextTask_ReturnsTasksInPriorityOrder() {
        scheduler.addTask(task1);
        scheduler.addTask(task2);
        scheduler.addTask(task3);

        // task2 should be polled first (highest priority)
        Task first = scheduler.pollNextTask();
        assertNotNull(first);
        assertEquals(2, first.getId(), "Highest priority task should be returned first");

        // Poll remaining and verify order
        Task second = scheduler.pollNextTask();
        Task third = scheduler.pollNextTask();

        assertNotNull(second);
        assertNotNull(third);
        assertTrue(second.getPriority() >= third.getPriority(),
                "Second task should have >= priority than third");
    }

    @Test
    void pollNextTask_ReturnsNull_WhenListIsEmpty() {
        Task result = scheduler.pollNextTask();
        assertNull(result, "pollNextTask should return null when list is empty");
    }

    @Test
    void pollNextTask_ReturnsNull_AfterAllTasksPolled() {
        scheduler.addTask(task1);
        scheduler.addTask(task2);

        scheduler.pollNextTask();
        scheduler.pollNextTask();

        Task result = scheduler.pollNextTask();
        assertNull(result, "pollNextTask should return null after all tasks are polled");
    }

    @Test
    void getAllScheduled_ReturnsAllTasksSorted() {
        scheduler.addTask(task1);
        scheduler.addTask(task2);
        scheduler.addTask(task3);

        List<Task> allTasks = scheduler.getAllScheduled();

        assertEquals(3, allTasks.size(), "getAllScheduled should return all added tasks");

        // Verify tasks are sorted by priority (descending)
        for (int i = 0; i < allTasks.size() - 1; i++) {
            assertTrue(allTasks.get(i).getPriority() >= allTasks.get(i + 1).getPriority(),
                    "Tasks should be sorted by priority descending");
        }
    }

    @Test
    void addTask_ThrowsNullPointerException_WhenTaskIsNull() {
        assertThrows(NullPointerException.class, () -> scheduler.addTask(null));
    }
}
