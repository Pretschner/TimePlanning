package zhuangyan.timeplanning.model;

import java.time.Duration;

/**
 * A Record representing a minimum spacing between the end of a Task of sourceGroup
 * and the start of a Task of targetGroup.
 */
public record GroupConstraint(Long id, String sourceGroup, String targetGroup, Duration minimumGap, Duration maximumGap) {
}
