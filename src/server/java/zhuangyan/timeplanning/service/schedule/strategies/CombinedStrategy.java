package zhuangyan.timeplanning.service.schedule.strategies;

import zhuangyan.timeplanning.model.Strategy;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.service.schedule.ScoringStrategy;

import java.util.*;

public class CombinedStrategy implements ScoringStrategy {
    /**
     * A Strategy based on the weighted sum of different strategies.
     */
    private final List<ScoringStrategy> strategies;
    private final Map<Strategy, Integer> weights;

    public CombinedStrategy(List<ScoringStrategy> strategies) {
        this.strategies = new ArrayList<>();
        weights = new HashMap<>();

        for (var strategy : strategies) {
            Strategy enumValue = toEnumValue(strategy);
            if (!weights.containsKey(enumValue)) {
                weights.put(enumValue, 1);
                this.strategies.add(strategy);
            }
            else {
                weights.compute(enumValue, (k, currentWeight) -> currentWeight + 1);
            }
        }
    }

    private Strategy toEnumValue(ScoringStrategy strategy) {
        if (strategy instanceof EarlyFinish) {
            return Strategy.Early_Finish;
        }
        else if (strategy instanceof FlowState) {
            return Strategy.Flow_State;
        }
        else if (strategy instanceof GroupedLeisure) {
            return Strategy.Grouped_Leisure;
        }
        else { // strategy instanceof MemorizableSchedule
            return Strategy.Memorizable_Schedule;
        }
    }

    @Override
    public double score(List<Integer> startSlots, List<Task> tasks) {
        double totalScore = strategies.stream()
                .mapToDouble(s -> s.score(startSlots, tasks) * weights.get(toEnumValue(s)))
                .sum();
        int numberOfStrategies = weights.values()
                .stream()
                .mapToInt(integer -> integer)
                .sum();
        return totalScore / numberOfStrategies;
    }
}
