package zhuangyan.timeplanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuangyan.timeplanning.model.*;
import zhuangyan.timeplanning.repository.BaseRepository;
import zhuangyan.timeplanning.service.schedule.EngineConfig;
import zhuangyan.timeplanning.service.schedule.SatEngine;
import zhuangyan.timeplanning.service.schedule.ScoringStrategy;
import zhuangyan.timeplanning.service.schedule.strategies.*;
import zhuangyan.timeplanning.time.TimeConverter;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    private final BaseRepository<Schedule> scheduleRepository;
    private final TaskService taskService;
    private final ConstraintService constraintService;

    @Autowired
    public ScheduleService(BaseRepository<Schedule> scheduleRepository, TaskService taskService, ConstraintService constraintService) {
        this.scheduleRepository = scheduleRepository;
        this.taskService = taskService;
        this.constraintService = constraintService;
    }

    public List<Schedule> createSchedule(int slotInMinutes, List<Strategy> strategies, int storedSolutions, int searchTime, long userId) {
        List<Task> tasks = taskService.getTasks(userId);
        List<GroupConstraint> groupConstraints = constraintService.getGroupConstraints(userId);
        TimeConverter converter = new TimeConverter(slotInMinutes);

        ScoringStrategy scoringStrategy;
        if (strategies == null || strategies.isEmpty()) {
            scoringStrategy = null;
        } else if (strategies.size() == 1) {
            scoringStrategy = initializeStrategy(strategies.get(0), converter);
        } else {
            List<ScoringStrategy> s = new ArrayList<>();
            for (var strategy : strategies) {
                s.add(initializeStrategy(strategy, converter));
            }
            scoringStrategy = new CombinedStrategy(s);
        }

        EngineConfig config = new EngineConfig(slotInMinutes, storedSolutions, searchTime, scoringStrategy, groupConstraints);
        List<List<TaskPlacement>> taskPlacements = SatEngine.schedule(tasks, config);
        List<Schedule> schedules = new ArrayList<>();
        for (var taskPlacement : taskPlacements) {
            Schedule schedule = new Schedule(scheduleRepository.getNextId(), taskPlacement);
            schedules.add(schedule);
        }
        scheduleRepository.deleteByUserId(userId);
        return scheduleRepository.save(schedules, userId, Schedule::id);
    }


    public List<Schedule> getSchedules(long userId) {
        return scheduleRepository.findByUserId(userId);
    }

    private ScoringStrategy initializeStrategy(Strategy strategy, TimeConverter converter) {
        return switch (strategy) {
            case Early_Finish -> new EarlyFinish(converter);
            case Flow_State -> new FlowState(converter);
            case Grouped_Leisure -> new GroupedLeisure(converter);
            case Memorizable_Schedule -> new MemorizableSchedule(converter);
        };
    }
}
