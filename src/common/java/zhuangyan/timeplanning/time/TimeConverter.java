package zhuangyan.timeplanning.time;

import zhuangyan.timeplanning.model.TimePoint;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

public class TimeConverter {
    /**
     * Tool for converting TimePoint and Duration Objects into their slot representation used in SatEngine.
     */

    private final int slotInMinutes;

    public TimeConverter(int slotInMinutes) {
        if (slotInMinutes <= 0) {
            throw new IllegalArgumentException(
                    "slotInMinutes must be positive."
            );
        }

        this.slotInMinutes = slotInMinutes;
    }

    public int fromTimePoint(TimePoint timePoint) {
        int day = timePoint.day().getValue() - 1; // Monday = 0

        int minutesOfDay =
                timePoint.time().getHour() * 60
                        + timePoint.time().getMinute();

        if (minutesOfDay % slotInMinutes != 0) {
            throw new IllegalArgumentException(
                    "TimePoint is not aligned to slot size: "
                            + timePoint
            );
        }

        int slotsPerDay = (24 * 60) / slotInMinutes;

        return day * slotsPerDay
                + (minutesOfDay / slotInMinutes);
    }

    public TimePoint toTimePoint(int slot) {
        int slotsPerDay = (24 * 60) / slotInMinutes;

        int dayIndex = slot / slotsPerDay;
        int slotOfDay = slot % slotsPerDay;

        DayOfWeek day = DayOfWeek.of(dayIndex + 1);

        int minutesOfDay = slotOfDay * slotInMinutes;

        int hour = minutesOfDay / 60;
        int minute = minutesOfDay % 60;

        return new TimePoint(
                day,
                LocalTime.of(hour, minute)
        );
    }

    public int fromDuration(Duration duration) {
        long minutes = duration.toMinutes();

        if (minutes <= 0) {
            throw new IllegalArgumentException(
                    "Duration must be positive."
            );
        }

        if (minutes % slotInMinutes != 0) {
            throw new IllegalArgumentException(
                    "Duration is not aligned to slot size: "
                            + duration
            );
        }

        return (int) (minutes / slotInMinutes);
    }

    public Duration toDuration(int slots) {
        if (slots < 0) {
            throw new IllegalArgumentException(
                    "Number of slots must be non-negative."
            );
        }

        return Duration.ofMinutes(
                (long) slots * slotInMinutes
        );
    }

    public int getSlotInMinutes() {
        return slotInMinutes;
    }

    public int getSlotsPerWeek() {
        return 7 * 24 * 60 / slotInMinutes;
    }
}
