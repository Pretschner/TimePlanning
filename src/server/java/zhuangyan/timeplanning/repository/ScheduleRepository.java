package zhuangyan.timeplanning.repository;

import org.springframework.stereotype.Repository;
import zhuangyan.timeplanning.model.Schedule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class ScheduleRepository {

    private final Map<Long, Schedule> schedules = new ConcurrentHashMap<>();
    private final Map<Long, Long> scheduleOwners = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public List<Schedule> save(List<Schedule> scheduleList, long userId) {
        lock.writeLock().lock();
        try {
            for (var schedule : scheduleList) {
                long id = schedule.id();
                schedules.put(id, schedule);
                scheduleOwners.put(id, userId);
            }
            return scheduleList;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Schedule> findById(long id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(schedules.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Schedule> findByUserId(long userId) {
        lock.readLock().lock();
        try {
            return scheduleOwners.entrySet().stream()
                    .filter(e -> e.getValue() == userId)
                    .map(e -> schedules.get(e.getKey()))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Schedule deleteById(long id) {
        lock.writeLock().lock();
        try {
            Schedule deletedSchedule = schedules.remove(id);
            scheduleOwners.remove(id);
            return deletedSchedule;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deleteByUserId(long userId) {
        lock.writeLock().lock();
        try {
            scheduleOwners.entrySet().stream()
                    .filter(e -> e.getValue() == userId)
                    .mapToLong(e -> e.getKey())
                    .forEach(id -> {
                        schedules.remove(id);
                        scheduleOwners.remove(id);
                    });
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Long> getOwnerId(long id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(scheduleOwners.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    public long getNextId() {
        return nextId.getAndIncrement();
    }
}