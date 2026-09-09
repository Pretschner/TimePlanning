package zhuangyan.timeplanning.view.util;

import java.time.Duration;
import java.time.LocalTime;

public class TimetableGrid {
    /**
     * Display-window grid for the client timetable: maps LocalTime values to
     * row indices and generates the row labels within a start/end window.
     */

    private final LocalTime start;
    private final int startMinute;
    private final LocalTime end;
    private final int endMinute;
    private final int slotInMinutes;

    public TimetableGrid(LocalTime start, LocalTime end, int slotInMinutes) {
        if (slotInMinutes <= 0) {
            throw new IllegalArgumentException(
                    "slotInMinutes must be positive."
            );
        }

        this.start = start;
        this.startMinute = start.toSecondOfDay() / 60;
        this.end = end;
        this.endMinute = (end.toSecondOfDay() / 60) + slotInMinutes;
        this.slotInMinutes = slotInMinutes;
    }

    public int rowIndexOf(long rowStartMinutes) {
        // Outside the displayed Range
        if (rowStartMinutes < startMinute || endMinute < rowStartMinutes) {
            return -1;
        }
        long minutesFromStart = rowStartMinutes - startMinute;
        return (int) (minutesFromStart / slotInMinutes);
    }

    public String[] rowLabels() {
        long totalMinutes = Duration.between(start, end).toMinutes();
        // No Slots for invalid Ranges
        if (totalMinutes <= 0) return new String[0];

        int count = (int) (totalMinutes / slotInMinutes);
        String[] slots = new String[count + 1];
        LocalTime current = start;
        for (int i = 0; i <= count; i++) {
            slots[i] = current.toString(); // e.g., "08:00"
            current = current.plusMinutes(slotInMinutes);
        }
        return slots;
    }
}
