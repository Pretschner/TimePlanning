package zhuangyan.timeplanning.service.schedule.strategies;

import zhuangyan.timeplanning.model.ScheduleConfig.Strategy;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.List;
import java.util.Objects;

public class FlowState extends ScoringStrategy {
    /**
     * A Strategy that rewards semantic clustering of activities,
     * facilitating a flow state of mind.
     */

    public FlowState(TimeConverter converter) {
        super(converter);
    }

    @Override
    public Strategy getType() {
        return Strategy.Flow_State;
    }

    @Override
    public double score(int[] startSlots, List<Task> tasks) {
        String[] allocatedArray = allocationArray(startSlots, tasks);
        double counter = 0.0;
        for (int i = 0; i < allocatedArray.length - 1; i++) {
            String t_1 = allocatedArray[i];
            String t_2 = allocatedArray[i + 1];
            if (!Objects.equals(t_1, t_2)) {
                counter++;
            }
        }
        return -counter;
    }
}
