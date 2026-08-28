package zhuangyan.timeplanning.model;

public record TimeWindow(TimePoint earliestStart, TimePoint latestEnd) {
    /**
     * A Frame of two Time Points.
     */
}
