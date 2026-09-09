package zhuangyan.timeplanning.service.schedule.strategies;

import zhuangyan.timeplanning.model.ScheduleConfig.Strategy;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.service.schedule.ScoringStrategy;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.List;

public class GroupedLeisure extends ScoringStrategy {
    /**
     * A Strategy that rewards grouped free time, avoiding scattered blocks of leisure.
     * It counts the number of boundaries between allocated and unallocated timeslots,
     * rewarding the minimum.
     */

    public GroupedLeisure(TimeConverter converter) {
        super(converter);
    }

    @Override
    public Strategy getType() {
        return Strategy.Grouped_Leisure;
    }

    @Override
    public double score(int[] startSlots, List<Task> tasks) {
        String[] allocatedArray = allocationArray(startSlots, tasks);
        double counter = 0.0;
        for (int i = 0; i < allocatedArray.length - 1; i++) {
            String t_1 = allocatedArray[i];
            String t_2 = allocatedArray[i + 1];
            if (t_1 == null || t_2 == null) {
                continue;
            }
            if (!t_1.equals(t_2) && (t_1.equals("Rest") ^ t_2.equals("Rest"))) {
                counter++;
            }
        }
        return -counter;
    }
}
