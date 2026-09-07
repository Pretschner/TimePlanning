package zhuangyan.timeplanning.view.renderers;

import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.model.TaskPlacement;

import java.time.DayOfWeek;
import java.time.LocalTime;

/** Record holding a task placement, its display slot type (name/time/background). Start/end times derived from placement. */
public record TaskSlot(TaskPlacement placement, SlotType type) {

    public Task task() { return placement.task(); }

    public LocalTime taskStart() { return placement.start().time(); }

    public LocalTime taskEnd() { return placement.end().time(); }

    public DayOfWeek day() { return placement.start().day(); }

    public DayOfWeek endDay() { return placement.end().day(); }
}