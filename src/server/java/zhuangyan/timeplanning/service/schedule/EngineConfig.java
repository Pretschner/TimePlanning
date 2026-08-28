package zhuangyan.timeplanning.service.schedule;

public record EngineConfig(int slotInMinutes, int storedSolutions, int searchTime, ScoringStrategy strategy) {
}
