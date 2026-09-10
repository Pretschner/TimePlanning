package zhuangyan.timeplanning.time;

import zhuangyan.timeplanning.model.TimePoint;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

public class TimeConverter {
    private final int slotInMinutes;
    private final int slotsPerDay;
    private final int slotsPerWeek;

    private final LocalTime viewStart;
    private final LocalTime viewEnd;
    private final int viewStartSlot;
    private final int viewEndSlot;
    private final int visibleRows;

    private TimeConverter(int slotInMinutes, LocalTime viewStart, LocalTime viewEnd) {
        if (slotInMinutes <= 0) {
            throw new IllegalArgumentException("slotInMinutes must be positive.");
        }
        this.slotInMinutes = slotInMinutes;
        this.slotsPerDay = (24 * 60) / slotInMinutes;
        this.slotsPerWeek = 7 * slotsPerDay;

        if (viewStart != null && viewEnd != null) {
            this.viewStart = viewStart;
            this.viewEnd = viewEnd;
            this.viewStartSlot = (viewStart.toSecondOfDay() / 60) / slotInMinutes;
            this.viewEndSlot = (viewEnd.toSecondOfDay() / 60) / slotInMinutes + 1;
            this.visibleRows = (int) Duration.between(viewStart, viewEnd).toMinutes() / slotInMinutes + 1;
        } else {
            this.viewStart = this.viewEnd = null;
            this.viewStartSlot = this.viewEndSlot = 0;
            this.visibleRows = 0;
        }
    }

    public static TimeConverter create(int slotInMinutes, LocalTime viewStart, LocalTime viewEnd) {
        return new TimeConverter(slotInMinutes, viewStart, viewEnd);
    }

    public static TimeConverter create(int slotInMinutes) {
        return new TimeConverter(slotInMinutes, null, null);
    }

    public int fromTimePoint(TimePoint timePoint) {
        int day = timePoint.day().getValue() - 1;

        int minutesOfDay =
                timePoint.time().getHour() * 60
                        + timePoint.time().getMinute();

        if (minutesOfDay % slotInMinutes != 0) {
            throw new IllegalArgumentException(
                    "TimePoint is not aligned to slot size: "
                            + timePoint
            );
        }

        return day * slotsPerDay
                + (minutesOfDay / slotInMinutes);
    }

    public TimePoint toTimePoint(int slot) {
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

    public int getSlotsPerWeek() {
        return slotsPerWeek;
    }

    public int getSlotsPerDay() {
        return slotsPerDay;
    }

    public int toRow(int slot) {
        int slotOfDay = slot % slotsPerDay;
        if (slotOfDay < viewStartSlot || slotOfDay >= viewEndSlot) return -1;
        return slotOfDay - viewStartSlot;
    }

    public String[] getRowLabels() {
        if (viewStart == null) return new String[0];
        String[] labels = new String[visibleRows];
        LocalTime current = viewStart;
        for (int i = 0; i < visibleRows; i++) {
            labels[i] = current.toString();
            current = current.plusMinutes(slotInMinutes);
        }
        return labels;
    }
}