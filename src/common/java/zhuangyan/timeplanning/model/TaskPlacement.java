package zhuangyan.timeplanning.model;

public record TaskPlacement(Task task, TimePoint start) {
    /**
     * Combined representation of a Task Object with the TimePoint of its schedules start.
     */
}