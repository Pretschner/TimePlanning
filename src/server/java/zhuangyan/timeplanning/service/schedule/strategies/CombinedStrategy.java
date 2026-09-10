package zhuangyan.timeplanning.service.schedule.strategies;

import zhuangyan.timeplanning.model.ScheduleConfig.Strategy;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.*;

public class CombinedStrategy extends ScoringStrategy {
    /**
     * A Strategy based on the weighted sum of different strategies.
     */
    private final List<ScoringStrategy> strategies;
    private final Map<Strategy, Integer> weights;

    public CombinedStrategy(List<ScoringStrategy> strategies) {
        super(TimeConverter.create(30)); // dummy, not used
        this.strategies = new ArrayList<>();
        weights = new HashMap<>();

        for (var strategy : strategies) {
            Strategy enumValue = strategy.getType();
            if (!weights.containsKey(enumValue)) {
                weights.put(enumValue, 1);
                this.strategies.add(strategy);
            } else {
                weights.compute(enumValue, (k, currentWeight) -> currentWeight + 1);
            }
        }
    }

    @Override
    public Strategy getType() {
        return null; // CombinedStrategy has no single type
    }

    @Override
    public double score(int[] startSlots, List<Task> tasks) {
        double totalScore = strategies.stream()
                .mapToDouble(s -> s.score(startSlots, tasks) * weights.get(s.getType()))
                .sum();
        int numberOfStrategies = weights.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
        return totalScore / numberOfStrategies;
    }
}