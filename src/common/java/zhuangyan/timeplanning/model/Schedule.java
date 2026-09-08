package zhuangyan.timeplanning.model;

import java.util.List;

/**
 * Core Representation of a Schedule, which is a (sorted) list of ScheduledTasks.
 */
public record Schedule(Long id, List<ScheduledTask> scheduledTasks) {
}
