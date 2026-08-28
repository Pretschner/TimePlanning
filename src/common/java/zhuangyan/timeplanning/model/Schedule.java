package zhuangyan.timeplanning.model;

import java.util.List;

public record Schedule(Long id, List<TaskPlacement> taskPlacements) {
    /**
     * Core Representation of a Schedule, which is a (sorted) list of TaskPlacements.
     */
}
