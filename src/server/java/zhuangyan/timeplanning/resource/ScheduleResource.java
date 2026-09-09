package zhuangyan.timeplanning.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhuangyan.timeplanning.model.Schedule;
import zhuangyan.timeplanning.model.ScheduleConfig;
import zhuangyan.timeplanning.service.AuthenticationService;
import zhuangyan.timeplanning.service.ScheduleService;
import zhuangyan.timeplanning.model.ScheduleConfig.Strategy;

import java.util.List;

@RestController
public class ScheduleResource {
    private final ScheduleService scheduleService;
    private final AuthenticationService authenticationService;

    @Autowired
    public ScheduleResource(ScheduleService scheduleService, AuthenticationService authenticationService) {
        this.scheduleService = scheduleService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/schedules")
    public ResponseEntity<List<Schedule>> createSchedule(@RequestBody ScheduleConfig config, @RequestHeader("Authorization") String authHeader) {
        int slotInMinutes = config.slotInMinutes();
        if (slotInMinutes <= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<Strategy> strategy = config.scoringStrategies();
        int storedSolutions = config.storedSolutions();
        int maximumSearchTime = config.searchTime();
        long userId = authenticationService.getUserId(authHeader);
        List<Schedule> createdSchedule = scheduleService.createSchedule(slotInMinutes, strategy, storedSolutions, maximumSearchTime, userId);
        return ResponseEntity.ok(createdSchedule);
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<Schedule>> getSchedules(@RequestHeader("Authorization") String authHeader) {
        long userId = authenticationService.getUserId(authHeader);
        return ResponseEntity.ok(scheduleService.getSchedules(userId));
    }
}
