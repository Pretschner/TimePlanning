package zhuangyan.timeplanning.service.schedule.engine;

import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.service.schedule.ScheduleFilter;
import zhuangyan.timeplanning.service.schedule.ScoringStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SolutionCollector extends CpSolverSolutionCallback {
    private final Variables variables;
    private final List<Task> tasks;

    private final int storedSolutions;
    // Store storedSolutions best solutions.
    private final PriorityQueue<ScoredSolution> bestSolutions;

    private final ScheduleFilter filter;
    private final ScoringStrategy strategy;

    public SolutionCollector(Variables variables, List<Task> tasks, ScoringStrategy strategy, ScheduleFilter filter, int storedSolutions) {
        this.variables = variables;
        this.tasks = tasks;

        this.storedSolutions = storedSolutions;
        this.bestSolutions = new PriorityQueue<>(this::compare);

        this.filter = filter;
        this.strategy = strategy;
    }

    @Override
    public void onSolutionCallback() {
        try {
            List<Integer> slotsCopy = copySlots(variables.startSlots());
            // Evaluate using filter and strategy.
            int violations = filter.amountViolations(slotsCopy, tasks);
            double score = strategy != null ? strategy.score(slotsCopy, tasks) : 0.0;
            // Store in bestSolutions.
            ScoredSolution solution = new ScoredSolution(slotsCopy, score, violations);
            if (bestSolutions.size() < storedSolutions) {
                bestSolutions.add(solution);
            } else if (compare(solution, bestSolutions.peek()) < 0) {
                bestSolutions.poll();
                bestSolutions.add(solution);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.err.println("Something went wrong in SatEngine.SolutionStore. Please investigate!");
        }
    }

    private List<Integer> copySlots(List<IntVar> startSlots) {
        ArrayList<Integer> slotsCopy = new ArrayList<>();
        for (int i = 0; i < startSlots.size(); i++) {
            IntVar cur = startSlots.get(i);
            slotsCopy.add((int) value(cur));
        }
        return slotsCopy;
    }

    private int compare(ScoredSolution a, ScoredSolution b) {
        int violations = Integer.compare(a.violations(), b.violations());
        if (violations != 0) {
            return violations;
        }
        return Double.compare(b.score(), a.score());
    }

    public List<ScoredSolution> bestSchedules() {
        return new ArrayList<>(bestSolutions);
    }
}
