package zhuangyan.timeplanning.service.schedule;

import zhuangyan.timeplanning.model.Strategy;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.List;

public abstract class ScoringStrategy {
    /**
     * Strategy Interface that lets the user decide what their "optimal" timetable would be.
     */
    protected final TimeConverter converter;

    protected ScoringStrategy(TimeConverter converter) {
        this.converter = converter;
    }

    public abstract double score(int[] startSlots, List<Task> tasks);

    public abstract Strategy getType();

    protected String[] allocationArray(int[] startSlots, List<Task> tasks) {
        int slotsPerWeek = converter.getSlotsPerWeek();
        String[] allocationArray = new String[slotsPerWeek];
        for (int i = 0; i < tasks.size(); i++) {
            String group = tasks.get(i).group();
            if (!group.equals("Sleep") && !group.equals("Rest")) {
                int startSlot = startSlots[i];
                int duration = converter.fromDuration(tasks.get(i).duration());
                for (int j = 0; j < duration; j++) {
                    int index = (startSlot + j) % slotsPerWeek;
                    allocationArray[index] = group;
                }
            }
        }
        return allocationArray;
    }
}
