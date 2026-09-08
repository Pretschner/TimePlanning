package zhuangyan.timeplanning.service.schedule.engine;

import java.util.List;

public record ScoredSolution(int[] slots, double score, int violations) {}