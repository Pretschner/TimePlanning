package zhuangyan.timeplanning.model;

/**
 * A Frame of two Time Points.
 */
public record TimeWindow(TimePoint earliestStart, TimePoint latestEnd) {
}
