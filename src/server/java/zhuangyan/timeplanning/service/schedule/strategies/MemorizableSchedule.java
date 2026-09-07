package zhuangyan.timeplanning.service.schedule.strategies;

import zhuangyan.timeplanning.model.Strategy;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.service.schedule.ScoringStrategy;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemorizableSchedule extends ScoringStrategy {
    /**
     * A Scheduling Strategy that rewards a recurring schedule,
     * that is the number of times a task with the same group has an identical start slot.
     */

    public MemorizableSchedule(TimeConverter converter) {
        super(converter);
    }

    @Override
    public Strategy getType() {
        return Strategy.Memorizable_Schedule;
    }

    @Override
    public double score(List<Integer> startSlots, List<Task> tasks) {
        Map<String, List<Integer>> buckets = new HashMap<>();
        double score = 0.0;

        for (int i = 0; i < tasks.size(); i++) {
            buckets.computeIfAbsent(tasks.get(i).group(), s -> new ArrayList<>()).add(i);
        }

        for (var group : buckets.entrySet()) {
            List<Integer> indices = group.getValue();
            for (int i = 0; i < indices.size(); i++) {
                for (int j = i + 1; j < indices.size(); j++) {

                    int firstIndex = indices.get(i);
                    int secondIndex = indices.get(j);

                    int firstSlot = startSlots.get(firstIndex);
                    int secondSlot = startSlots.get(secondIndex);

                    if (isIdentical(firstSlot, secondSlot)) {
                        score++;
                    }
                }
            }
        }
        return score;
    }

    private boolean isIdentical(int firstSlot, int secondSlot) {
        int slotsPerDay = converter.getSlotsPerWeek() / 7;
        return (firstSlot % slotsPerDay) == (secondSlot % slotsPerDay);
    }
}
