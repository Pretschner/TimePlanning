package zhuangyan.timeplanning.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhuangyan.timeplanning.exception.BadRequestException;
import zhuangyan.timeplanning.model.Schedule;
import zhuangyan.timeplanning.model.ScheduleConfig;
import zhuangyan.timeplanning.service.AuthenticationService;
import zhuangyan.timeplanning.service.ScheduleService;

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
        if (config.slotInMinutes() <= 0) {
            throw new BadRequestException("Slot duration must be positive.");
        }
        long userId = authenticationService.getUserId(authHeader);
        List<Schedule> createdSchedule = scheduleService.createSchedule(config, userId);
        return ResponseEntity.ok(createdSchedule);
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<Schedule>> getSchedules(@RequestHeader("Authorization") String authHeader) {
        long userId = authenticationService.getUserId(authHeader);
        return ResponseEntity.ok(scheduleService.getSchedules(userId));
    }
}