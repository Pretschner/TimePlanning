package zhuangyan.timeplanning.service.schedule.engine;

import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.service.schedule.strategies.ScoringStrategy;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.List;

public record EngineConfig(TimeConverter converter, int storedSolutions, int searchTime, ScoringStrategy strategy, List<GroupConstraint> constraints) {
}