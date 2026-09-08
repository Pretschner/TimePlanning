package zhuangyan.timeplanning.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Duration;

/**
 * A Task fixed at a specific start TimePoint (end computed from duration).
 * Replaces TaskPlacement and TaskSlot as the single "scheduled task" representation.
 */
public record ScheduledTask(Task task, TimePoint start, TimePoint end) {

    public ScheduledTask(Task task, TimePoint start) {
        this(task, start, computeEnd(start, task.duration()));
    }

    private static TimePoint computeEnd(TimePoint start, Duration duration) {
        LocalTime endTime = start.time().plus(duration);
        DayOfWeek endDay = start.day();
        if (endTime.isBefore(start.time()) || endTime.equals(LocalTime.MIDNIGHT)) {
            endDay = DayOfWeek.of((endDay.getValue() % 7) + 1);
        }
        return new TimePoint(endDay, endTime);
    }
}