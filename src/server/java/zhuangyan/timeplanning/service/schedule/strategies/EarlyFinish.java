package zhuangyan.timeplanning.service.schedule.strategies;

import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.service.schedule.EngineConfig;
import zhuangyan.timeplanning.service.schedule.ScoringStrategy;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.List;

public class EarlyFinish implements ScoringStrategy {
    /**
     * A Strategy that rewards an early end of tasks in the evening,
     * avoiding late night hustling.
     * It averages the clock-off times during the week.
     */
    private final TimeConverter converter;

    public EarlyFinish(TimeConverter converter) {
        this.converter = converter;
    }

    public EarlyFinish(EngineConfig config) {
        this.converter = new TimeConverter(config.slotInMinutes());
    }

    @Override
    public double score(List<Integer> startSlots, List<Task> tasks) {
        String[] allocatedArray = allocationArray(startSlots, tasks, converter);
        double counter = 0.0;
        int slotsPerDay = converter.getSlotsPerWeek() / 7; // assuming integer division
        for (int day = 0; day < 7; day++) {
            int endOfDay = (day + 1) * slotsPerDay - 1;
            int startOfDay = day * slotsPerDay;
            for (int j = endOfDay; j >= startOfDay; j--) {
                if (allocatedArray[j] == null) {
                    counter++;
                } else {
                    break;
                }
            }
        }
        // Average over the days.
        return counter/7;
    }
}
