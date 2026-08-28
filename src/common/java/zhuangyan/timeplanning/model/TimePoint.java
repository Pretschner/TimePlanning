package zhuangyan.timeplanning.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record TimePoint(DayOfWeek day, LocalTime time) {
    /**
     * Custom Representation of a Time Point in a Week.
     */
    @Override
    public String toString() {
        return day.toString().substring(0, 3) + ", " + time.toString();
    }
}
