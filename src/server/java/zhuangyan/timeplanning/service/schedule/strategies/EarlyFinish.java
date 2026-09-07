package zhuangyan.timeplanning.service.schedule.strategies;

import zhuangyan.timeplanning.model.Strategy;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.service.schedule.ScoringStrategy;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.List;

public class EarlyFinish extends ScoringStrategy {
    /**
     * A Strategy that rewards an early end of tasks in the evening,
     * avoiding late night hustling.
     * It averages the clock-off times during the week.
     */

    public EarlyFinish(TimeConverter converter) {
        super(converter);
    }

    @Override
    public Strategy getType() {
        return Strategy.Early_Finish;
    }

    @Override
    public double score(List<Integer> startSlots, List<Task> tasks) {
        String[] allocatedArray = allocationArray(startSlots, tasks);
        double counter = 0.0;
        int slotsPerDay = converter.getSlotsPerWeek() / 7;
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
        return counter / 7;
    }
}
