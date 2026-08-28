package zhuangyan.timeplanning.service.schedule.strategies;

import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.service.schedule.EngineConfig;
import zhuangyan.timeplanning.service.schedule.ScoringStrategy;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.List;
import java.util.Objects;

public class FlowState implements ScoringStrategy {
    /**
     * A Strategy that rewards semantic clustering of activities,
     * facilitating a flow state of mind.
     */
    private final TimeConverter converter;

    public FlowState(TimeConverter converter) {
        this.converter = converter;
    }

    public FlowState(EngineConfig config) {
        this.converter = new TimeConverter(config.slotInMinutes());
    }

    @Override
    public double score(List<Integer> startSlots, List<Task> tasks) {
        String[] allocatedArray = allocationArray(startSlots, tasks, converter);
        double counter = 0.0;
        for (int i = 0; i < allocatedArray.length - 1; i++) {
            String t_1 = allocatedArray[i];
            String t_2 = allocatedArray[i+1];
            if (!Objects.equals(t_1, t_2)) {
                counter++;
            }
        }
        return -counter;
    }
}
