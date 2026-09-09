package zhuangyan.timeplanning.service.schedule.engine;

import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.service.schedule.strategies.ScoringStrategy;

import java.util.List;

public record EngineConfig(int slotInMinutes, int storedSolutions, int searchTime, ScoringStrategy strategy, List<GroupConstraint> constraints) {
}
