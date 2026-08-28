package zhuangyan.timeplanning.model;

import java.time.Duration;

public record Task (Long id, String name, String group, Duration duration, TimeWindow timeWindow) {
    /**
     * Core Representation of a Task, and it's inherent constraints (duration and time window).
     */
}
