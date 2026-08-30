package zhuangyan.timeplanning.time;

import java.time.Duration;
import java.time.LocalTime;

public class TimetableGrid {
    /**
     * Display-window grid for the client timetable: maps LocalTime values to
     * row indices and generates the row labels within a start/end window.
     */

    private final LocalTime start;
    private final LocalTime end;
    private final int slotInMinutes;

    public TimetableGrid(LocalTime start, LocalTime end, int slotInMinutes) {
        if (slotInMinutes <= 0) {
            throw new IllegalArgumentException(
                    "slotInMinutes must be positive."
            );
        }

        this.start = start;
        this.end = end;
        this.slotInMinutes = slotInMinutes;
    }

    public int rowIndexOf(LocalTime time) {
        // Outside the displayed Range
        if (time.isBefore(start) || time.isAfter(end)) {
            return -1;
        }
        long minutesFromStart = Duration.between(start, time).toMinutes();
        return (int) (minutesFromStart / slotInMinutes);
    }

    public String[] rowLabels() {
        long totalMinutes = Duration.between(start, end).toMinutes();
        // No Slots for invalid Ranges
        if (totalMinutes <= 0) return new String[0];

        int count = (int) (totalMinutes / slotInMinutes);
        String[] slots = new String[count];
        LocalTime current = start;
        for (int i = 0; i < count; i++) {
            slots[i] = current.toString(); // e.g., "08:00"
            current = current.plusMinutes(slotInMinutes);
        }
        return slots;
    }

    public int getRowCount() {
        return rowLabels().length;
    }
}
