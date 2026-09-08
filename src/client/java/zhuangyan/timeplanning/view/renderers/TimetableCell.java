package zhuangyan.timeplanning.view.renderers;

import zhuangyan.timeplanning.model.ScheduledTask;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.model.TimePoint;

import java.time.LocalTime;

/**
 * View model for a single timetable cell.
 * Encapsulates what to render (name, time range, or background) for a task at a specific grid row.
 */
public record TimetableCell(ScheduledTask scheduledTask, SlotType type) {

    public Task task() { return scheduledTask.task(); }
    public TimePoint start() { return scheduledTask.start(); }
    public TimePoint end() { return scheduledTask.end(); }

    /** Visual role of this cell in the timetable grid. */
    public enum SlotType {
        NAME_DISPLAY,   // First visible row: shows task name
        TIME_DISPLAY,   // Second visible row: shows "HH:mm -> HH:mm"
        BACKGROUND      // Remaining rows: colored background only
    }
}