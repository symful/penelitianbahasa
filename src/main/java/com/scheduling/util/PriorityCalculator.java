package com.scheduling.util;

import com.scheduling.model.Task;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class PriorityCalculator {

    public static final double DEFAULT_LKS_WEIGHT = 1.0;
    public static final double DEFAULT_DIFFICULTY_WEIGHT = 1.0;
    public static final double DEFAULT_URGENCY_WEIGHT = 1.0;
    public static final double DEFAULT_TIME_WEIGHT = 1.0;

    private static final double[] DEFAULT_WEIGHTS = {
        DEFAULT_LKS_WEIGHT,
        DEFAULT_DIFFICULTY_WEIGHT,
        DEFAULT_URGENCY_WEIGHT,
        DEFAULT_TIME_WEIGHT
    };

    private static final double URGENCY_CAP = 1.0;

    private PriorityCalculator() {
        // Utility class, no instantiation
    }

    public static double calculatePriority(Task task) {
        return calculatePriority(task, DEFAULT_WEIGHTS);
    }

    public static double calculatePriority(Task task, double[] weights) {
        if (task == null || weights == null || weights.length < 4) {
            return 0.0;
        }

        double w1 = weights[0];
        double w2 = weights[1];
        double w3 = weights[2];
        double w4 = weights[3];

        double lks = task.getLksCount();
        double difficulty = task.getDifficulty();
        double estimatedMinutes = task.getEstimatedMinutes();

        double urgency = calculateUrgency(task.getDeadline());

        double timeComponent = estimatedMinutes / 60.0;

        return w1 * lks + w2 * difficulty + w3 * urgency + w4 * timeComponent;
    }

    static double calculateUrgency(LocalDateTime deadline) {
        if (deadline == null) {
            return 0.0;
        }

        LocalDateTime now = LocalDateTime.now();
        long daysUntilDeadline = ChronoUnit.DAYS.between(now, deadline);

        double urgency;
        if (daysUntilDeadline < 0) {
            urgency = URGENCY_CAP;
        } else {
            urgency = 1.0 / (daysUntilDeadline + 1);
        }

        return urgency;
    }
}
