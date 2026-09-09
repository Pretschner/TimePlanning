package zhuangyan.timeplanning.service.schedule;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import com.google.ortools.util.Domain;
import zhuangyan.timeplanning.exception.NotFoundException;
import zhuangyan.timeplanning.model.*;
import zhuangyan.timeplanning.service.schedule.engine.*;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.*;

public class SatEngine {
    /**
     * The Core Business Logic using Google's CP-SAT Tool to identify a viable timetable for a given list of tasks.
     * It generates all possible solutions which are rated by the callback store, returning the "optimal" schedule
     * according to the ScoringStrategy.
     */

    public static List<List<ScheduledTask>> schedule(List<Task> tasks, EngineConfig config) {
        Loader.loadNativeLibraries();
        CpModel model = new CpModel();

        Variables variables = createVariables(model, tasks, config);

        applyNoOverlap(model, variables);

        // Solve
        SolutionCollector store = new SolutionCollector(variables, tasks, config);
        CpSolver solver = new CpSolver();
        solver.getParameters().setEnumerateAllSolutions(true);
        solver.getParameters().setMaxTimeInSeconds(config.searchTime());
        solver.solve(model, store); // Callback to Store on Solution

        // Return Placements
        List<ScoredSolution> bestSolutions = store.bestSchedules();

        return mapToScheduledTasks(bestSolutions, tasks, config);
    }

    public static Variables createVariables(CpModel model, List<Task> tasks, EngineConfig config) {
        TimeConverter converter = new TimeConverter(config.slotInMinutes());
        List<IntVar> startSlots = new ArrayList<>();
        List<IntervalVar> intervals = new ArrayList<>();
        List<IntervalVar> shiftedIntervals = new ArrayList<>();
        // Add variables
        for (Task task : tasks) {
            int duration = converter.fromDuration(task.duration());

            TimeWindow window = task.timeWindow();

            int earliestStart = converter.fromTimePoint(window.earliestStart());
            int latestStart = converter.fromTimePoint(window.latestEnd()) - duration;

            IntVar start;
            // Normal window
            if (earliestStart <= latestStart) {
                start = model.newIntVar(earliestStart, latestStart, task.name() + "_start");
            }
            // Wrapped window
            else {
                Domain domain = Domain.fromIntervals(new long[][]{{earliestStart, converter.getSlotsPerWeek() - 1}, {0, latestStart}});
                start = model.newIntVarFromDomain(domain, task.name() + "_start");
            }
            startSlots.add(start);

            // Actual task interval: [start, start + duration)
            IntervalVar interval = model.newFixedSizeIntervalVar(start, duration, task.name() + "_interval");
            intervals.add(interval);

            // Same interval shifted by 1 week.
            IntVar shiftedStart = model.newIntVar(converter.getSlotsPerWeek(), 2L * converter.getSlotsPerWeek() - 1, task.name() + "_shifted_start");
            model.addEquality(shiftedStart, LinearExpr.affine(start, 1, converter.getSlotsPerWeek()));

            IntervalVar shiftedInterval = model.newFixedSizeIntervalVar(shiftedStart, duration, task.name() + "_shifted_interval");
            shiftedIntervals.add(shiftedInterval);
        }
        return new Variables(startSlots, intervals, shiftedIntervals);
    }

    public static void applyNoOverlap(CpModel model, Variables variables) {
        List<IntervalVar> allIntervals = new ArrayList<>();
        allIntervals.addAll(variables.intervals());
        allIntervals.addAll(variables.shiftedIntervals());
        model.addNoOverlap(allIntervals);
    }

    public static List<List<ScheduledTask>> mapToScheduledTasks(List<ScoredSolution> bestSolutions, List<Task> tasks, EngineConfig config) {
        TimeConverter converter = new TimeConverter(config.slotInMinutes());
        List<List<ScheduledTask>> schedules = new ArrayList<>();

        while (!bestSolutions.isEmpty()) {
            ScoredSolution currentSolution = bestSolutions.remove(0);
            int[] slots = currentSolution.slots();

            List<ScheduledTask> currentSchedule = new ArrayList<>();

            for (int i = 0; i < tasks.size(); i++) {
                int slot = slots[i];
                TimePoint start = converter.toTimePoint(slot);
                currentSchedule.add(new ScheduledTask(tasks.get(i), start));
            }

            currentSchedule.sort(Comparator.comparingInt(a -> converter.fromTimePoint(a.start())));
            schedules.add(currentSchedule);
        }

        if (schedules.isEmpty()) {
            throw new NotFoundException("No solution was found.");
        }

        return schedules;
    }
}

