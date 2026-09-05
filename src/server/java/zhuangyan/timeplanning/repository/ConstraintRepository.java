package zhuangyan.timeplanning.repository;

import org.springframework.stereotype.Repository;
import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.service.schedule.CsvParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class ConstraintRepository {
    private final Map<Long, GroupConstraint> constraints;
    private final Map<Long, Long> constraintOwners;
    private final AtomicLong nextId = new AtomicLong(1);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ConstraintRepository() {
        constraints = new ConcurrentHashMap<>();
        constraintOwners = new ConcurrentHashMap<>();
        CsvParser parser = new CsvParser();
        try {
            List<GroupConstraint> constraints = parser.readConstraint(Path.of("constraints.csv"));
            for (var c : constraints) {
                save(c, 1);
                nextId.getAndIncrement();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GroupConstraint save(GroupConstraint constraint, long userId) {
        lock.writeLock().lock();
        try {
            constraints.put(constraint.id(), constraint);
            constraintOwners.put(constraint.id(), userId);
            return constraint;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<GroupConstraint> findByUserId(long userId) {
        lock.readLock().lock();
        try {
            return constraintOwners.entrySet().stream()
                    .filter(e -> e.getValue() == userId)
                    .map(e -> constraints.get(e.getKey()))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public GroupConstraint deleteById(long id) {
        lock.writeLock().lock();
        try {
            GroupConstraint deletedGroupConstraint = constraints.remove(id);
            constraintOwners.remove(id);
            return deletedGroupConstraint;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Long> getOwnerId(long id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(constraintOwners.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    public long getNextId() {
        return nextId.getAndIncrement();
    }
}