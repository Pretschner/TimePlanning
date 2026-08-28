package zhuangyan.timeplanning.service.schedule.engine;

import java.util.List;

public record ScoredSolution(List<Integer> slots, double score, int violations) {}