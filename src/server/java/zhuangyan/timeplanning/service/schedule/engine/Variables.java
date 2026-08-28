package zhuangyan.timeplanning.service.schedule.engine;

import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.IntervalVar;

import java.util.List;

public record Variables(List<IntVar> startSlots, List<IntervalVar> intervals, List<IntervalVar> shiftedIntervals) { }