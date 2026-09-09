package zhuangyan.timeplanning.model;

import java.util.List;
/**
 * Configuration Body for Schedule Creation and Selection
 * @param slotInMinutes the length of a single time slot that can be allocated (in minutes)
 * @param scoringStrategies the strategies according to which the best solutions are picked
 * @param storedSolutions the amount of solutions stored and sent back (in descending order of score)
 * @param searchTime the maximum search time of Google's SAT Solver
 */
public record ScheduleConfig(int slotInMinutes, List<Strategy> scoringStrategies, int storedSolutions, int searchTime) {
    /**
     * Enum representing different Strategy Classes to avoid tight coupling.
     */
    public enum Strategy {
        Early_Finish,
        Flow_State,
        Grouped_Leisure,
        Memorizable_Schedule
    }
}

