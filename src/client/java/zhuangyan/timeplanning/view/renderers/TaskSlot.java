package zhuangyan.timeplanning.view.renderers;

import zhuangyan.timeplanning.model.Task;

import java.time.LocalTime;

public record TaskSlot(Task task, SlotType type, LocalTime taskStart, LocalTime taskEnd) {
}
