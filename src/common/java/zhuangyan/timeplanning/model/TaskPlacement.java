package zhuangyan.timeplanning.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Combined representation of a Task Object with the TimePoint of its scheduled start and end.
 */
public record TaskPlacement(Task task, TimePoint start, TimePoint end) {

    public TaskPlacement(Task task, TimePoint start) {
        this(task, start, computeEnd(start, task.duration()));
    }

    private static TimePoint computeEnd(TimePoint start, java.time.Duration duration) {
        LocalTime endTime = start.time().plus(duration);
        DayOfWeek endDay = start.day();
        if (endTime.isBefore(start.time()) || endTime.equals(LocalTime.MIDNIGHT)) {
            endDay = DayOfWeek.of((endDay.getValue() % 7) + 1);
        }
        return new TimePoint(endDay, endTime);
    }
}