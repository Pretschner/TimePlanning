package zhuangyan.timeplanning.service.schedule;

import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ScheduleFilter {
    /**
     * A binary pass or fail filter for post-selection on proposed task schedules.
     * It checks whether the spacing constraints between various groups are met.
     */

    private final TimeConverter converter;
    private final List<GroupConstraint> constraints;

    public ScheduleFilter(TimeConverter converter, List<GroupConstraint> constraints) {
        this.converter = converter;
        this.constraints = constraints == null ? List.of() : constraints;
    }

    public ScheduleFilter(EngineConfig config) {
        converter = new TimeConverter(config.slotInMinutes());
        constraints = config.constraints();
    }

    public boolean zeroViolations(List<Integer> slots, List<Task> tasks) {
        return accept(slots, tasks, true) == 0;
    }

    public int amountViolations(List<Integer> slots, List<Task> tasks) {
        return accept(slots, tasks, false);
    }

    private int accept(List<Integer> slots, List<Task> tasks, boolean shortCircuit) {
        List<ScheduledTask> schedule = convert(slots, tasks);
        int count = 0;
        for (GroupConstraint constraint : constraints) {

            String sourceGroup = constraint.sourceGroup().trim().toLowerCase(Locale.ROOT);
            String targetGroup = constraint.targetGroup().trim().toLowerCase(Locale.ROOT);
            int minimumGap = constraint.minimumGap() != null ? converter.fromDuration(constraint.minimumGap()) : Integer.MIN_VALUE;
            int maximumGap = constraint.maximumGap() != null ? converter.fromDuration(constraint.maximumGap()) : Integer.MAX_VALUE;

            for (ScheduledTask target : schedule) {

                if (!target.task().group().trim().toLowerCase(Locale.ROOT).equals(targetGroup)) {
                    continue;
                }

                ScheduledTask previousSource = getPreviousSource(target, schedule, sourceGroup);

                if (previousSource == null) {
                    continue;
                }

                int gap = target.startSlot() - previousSource.endSlot();

                if (gap < 0) {
                    gap += converter.getSlotsPerWeek();
                }

                if (gap < minimumGap || gap > maximumGap) {
                    count++;
                    if (shortCircuit) {
                        return count;
                    }
                }
            }
        }
        return count;
    }

    private ScheduledTask getPreviousSource(ScheduledTask target, List<ScheduledTask> schedule, String sourceGroup) {
        ScheduledTask previousSource = null;

        for (ScheduledTask candidate : schedule) {
            if (!candidate.task().group().trim().toLowerCase(Locale.ROOT).equals(sourceGroup)) {
                continue;
            }
            if (candidate.startSlot() < target.startSlot()) {
                previousSource = candidate;
            }
        }

        // Wraparound case:
        // no source before target -> use last source
        if (previousSource == null) {
            for (ScheduledTask candidate : schedule) {
                if (!candidate.task().group().trim().toLowerCase(Locale.ROOT).equals(sourceGroup)) {
                    continue;
                }
                previousSource = candidate;
            }
        }

        return previousSource;
    }

    private List<ScheduledTask> convert(List<Integer> slots, List<Task> tasks) {
        List<ScheduledTask> schedule = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            int startSlot = slots.get(i);
            int duration = converter.fromDuration(task.duration());

            int endSlot = (startSlot + duration) % converter.getSlotsPerWeek();

            schedule.add(new ScheduledTask(task, startSlot, endSlot));
        }

        schedule.sort(Comparator.comparingInt(ScheduledTask::startSlot));
        return schedule;
    }

    private record ScheduledTask(
            Task task,
            int startSlot,
            int endSlot
    ) {}
}