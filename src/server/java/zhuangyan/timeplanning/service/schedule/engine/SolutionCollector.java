package zhuangyan.timeplanning.service.schedule.engine;

import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.service.schedule.ScheduleFilter;
import zhuangyan.timeplanning.service.schedule.ScoringStrategy;

import java.util.ArrayList;
import java.util.Collections;
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

    // Internal flag: Short Circuit the filter computation if all (-> the worst) solutions have 0 violations
    // -> use filter.accept() + violation > 0 -> no score and eviction
    private boolean shortCircuit = false;

    @Override
    public void onSolutionCallback() {
        try {
            List<Integer> slotsCopy = copySlots(variables.startSlots());

            // Kick solutions with > 0 violations out if the best solutions already have 0 violations at most
            if (shortCircuit && !filter.zeroViolations(slotsCopy, tasks)) return;

            // Evaluate using filter and strategy.
            int violations = shortCircuit ? 0 : filter.amountViolations(slotsCopy, tasks);
            double score = strategy != null ? strategy.score(slotsCopy, tasks) : 0.0;

            // Store in bestSolutions.
            ScoredSolution solution = new ScoredSolution(slotsCopy, score, violations);
            if (bestSolutions.size() < storedSolutions) {
                bestSolutions.add(solution);
            }
            // Sorted in Ascending Order (worst < bad < good < best) -> if worst < current
            else if (compare(bestSolutions.peek(), solution) < 0) { // Not null, since size() == storedSolutions
                if (!shortCircuit) {
                    shortCircuit = bestSolutions.poll().violations() == 0;
                }
                else {
                    bestSolutions.poll();
                }
                bestSolutions.add(solution);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
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

    // a < b <=> b.violations() < a.violations() (if !=) or a.score() < b.score() (if ==)
    private int compare(ScoredSolution a, ScoredSolution b) {
        int violations = Integer.compare(b.violations(), a.violations());
        if (violations != 0) {
            return violations;
        }
        return Double.compare(a.score(), b.score());
    }

    // Descending Order (Head: best -> Tail: worst)
    public List<ScoredSolution> bestSchedules() {
        ArrayList<ScoredSolution> solutions = new ArrayList<>(bestSolutions);
        Collections.reverse(solutions);
        return solutions;
    }
}
