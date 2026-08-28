package zhuangyan.timeplanning.service.schedule;

import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.List;

public interface ScoringStrategy {
    /**
     * Strategy Interface that lets the user decide what their "optimal" timetable would be.
     */

    double score(List<Integer> startSlots, List<Task> tasks);

    default String[] allocationArray(List<Integer> startSlots, List<Task> tasks, TimeConverter converter){
        int slotsPerWeek = converter.getSlotsPerWeek();
        String[] allocationArray = new String[slotsPerWeek];
        for (int i = 0; i < tasks.size(); i++) {
            String group = tasks.get(i).group();
            if (!group.equals("Sleep") && !group.equals("Rest")) {
                int startSlot = startSlots.get(i);
                int duration = converter.fromDuration(tasks.get(i).duration());
                for (int j = 0; j < duration; j++) {
                    int index = (startSlot + j) % slotsPerWeek;  // wrap around
                    allocationArray[index] = group;
                }
            }
        }
        return allocationArray;
    }
}
