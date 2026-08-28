package zhuangyan.timeplanning.repository;

import org.springframework.stereotype.Repository;
import zhuangyan.timeplanning.model.Schedule;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ScheduleRepository {

    private final Map<Long, Schedule> schedules = new ConcurrentHashMap<>(); // id -> Schedule
    private final Map<Long, Long> scheduleOwners = new ConcurrentHashMap<>(); // scheduleId -> userId
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Schedule> save(List<Schedule> scheduleList, long userId) {
        // Create and Save new Schedules
        for (var schedule : scheduleList) {
            long id = schedule.id();
            schedules.put(id, schedule);
            scheduleOwners.put(id, userId);
        }
        return scheduleList;
    }

    public Optional<Schedule> findById(long id) {
        return Optional.ofNullable(schedules.get(id));
    }

    public List<Schedule> findByUserId(long userId) {
        return scheduleOwners.entrySet().stream()
                .filter(e -> e.getValue() == userId)
                .map(e -> schedules.get(e.getKey()))
                .toList();
    }

    public Schedule deleteById(long id) {
        Schedule deletedSchedule = schedules.remove(id);
        scheduleOwners.remove(id);
        return deletedSchedule;
    }

    public void deleteByUserId(long userId) {
        // Delete old Schedules
        scheduleOwners.entrySet().stream()
                .filter(e -> e.getValue() == userId)
                .mapToLong(e -> e.getKey())
                .forEach(id -> {
                    schedules.remove(id);
                    scheduleOwners.remove(id);
                });
    }

    public Optional<Long> getOwnerId(long id) {
        return Optional.ofNullable(scheduleOwners.get(id));
    }

    public long getNextId() {
        return nextId.getAndIncrement();
    }
}
