package zhuangyan.timeplanning.service.schedule.engine;

import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.service.schedule.ScheduleFilter;
import zhuangyan.timeplanning.service.schedule.strategies.ScoringStrategy;

import java.util.*;

public class SolutionCollector extends CpSolverSolutionCallback {
    private final Variables variables;
    private final List<Task> tasks;

    private final int storedSolutions;
    // Store storedSolutions best solutions.
    private final PriorityQueue<ScoredSolution> bestSolutions;

    private final ScheduleFilter filter;
    private final ScoringStrategy strategy;

    private final int[] slots;

    public SolutionCollector(Variables variables, List<Task> tasks, EngineConfig config) {
        this.variables = variables;
        this.tasks = tasks;

        this.storedSolutions = config.storedSolutions();
        this.bestSolutions = new PriorityQueue<>(this::compare);

        this.filter = new ScheduleFilter(config.converter(), config.constraints());
        this.strategy = config.strategy();

        this.slots = new int[variables.startSlots().size()];
    }

    // Internal flag: Short Circuit the filter computation if all (-> the worst) solutions have 0 violations
    // -> use filter.accept() + violation > 0 -> no score and eviction
    private boolean shortCircuit = false;

    @Override
    public void onSolutionCallback() {
        try {
            copySlots(variables.startSlots());

            // Kick solutions with > 0 violations out if the best solutions already have 0 violations at most
            if (shortCircuit && !filter.zeroViolations(slots, tasks)) return;

            // Evaluate using filter and strategy.
            int violations = shortCircuit ? 0 : filter.amountViolations(slots, tasks);
            double score = strategy != null ? strategy.score(slots, tasks) : 0.0;

            // Store in bestSolutions.

            if (bestSolutions.size() < storedSolutions) {

                ScoredSolution solution = new ScoredSolution(Arrays.copyOf(slots, slots.length), score, violations);// Not null, since size() == storedSolutions
                bestSolutions.add(solution);
            }
            // Sorted in Ascending Order (worst < bad < good < best) -> if worst < current
            else if (compare(bestSolutions.peek(), violations, score) < 0) {
                // Update Short Circuiting Flag
                if (!shortCircuit) {
                    shortCircuit = bestSolutions.poll().violations() == 0;
                }
                else {
                    bestSolutions.poll();
                }

                ScoredSolution solution = new ScoredSolution(Arrays.copyOf(slots, slots.length), score, violations);// Not null, since size() == storedSolutions
                bestSolutions.add(solution);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void copySlots(List<IntVar> startSlots) {
        for (int i = 0; i < startSlots.size(); i++) {
            IntVar cur = startSlots.get(i);
            slots[i] = (int) value(cur);
        }
    }


    // a < b <=> b.violations() < a.violations() (if !=) or a.score() < b.score() (if ==)
    private int compare(ScoredSolution a, ScoredSolution b) {
        int violations = Integer.compare(b.violations(), a.violations());
        if (violations != 0) {
            return violations;
        }
        return Double.compare(a.score(), b.score());
    }

    private int compare(ScoredSolution a, int violations, double score) {
        int compareViolations = Integer.compare(violations, a.violations());
        if (compareViolations != 0) {
            return compareViolations;
        }
        return Double.compare(a.score(), score);
    }

    // Descending Order (Head: best -> Tail: worst)
    public List<ScoredSolution> bestSchedules() {
        ArrayList<ScoredSolution> solutions = new ArrayList<>(bestSolutions);
        Collections.reverse(solutions);
        return solutions;
    }
}