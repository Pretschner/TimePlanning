package zhuangyan.timeplanning.service.schedule;

import zhuangyan.timeplanning.model.GroupConstraint;

import java.util.List;

public record EngineConfig(int slotInMinutes, int storedSolutions, int searchTime, ScoringStrategy strategy, List<GroupConstraint> constraints) {
}
