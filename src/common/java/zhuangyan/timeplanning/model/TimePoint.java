package zhuangyan.timeplanning.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Locale;

/**
 * Custom Representation of a Time Point in a Week.
 */
public record TimePoint(DayOfWeek day, LocalTime time) {
    @Override
    public String toString() {
        return day.toString().substring(0, 3) + ", " + time.toString();
    }

    public String toPrettyString() {
        return day.toString().charAt(0) + day.toString().substring(1).toLowerCase(Locale.ROOT) + ", " + time.toString();
    }
}
