package zhuangyan.timeplanning.view.renderers;

import zhuangyan.timeplanning.model.Task;

import java.time.LocalTime;

/** Record holding a task, its display slot type (name/time/background), and start/end times for the cell. */
public record TaskSlot(Task task, SlotType type, LocalTime taskStart, LocalTime taskEnd) {
}
