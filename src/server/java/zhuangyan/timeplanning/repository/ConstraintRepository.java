package zhuangyan.timeplanning.repository;

import org.springframework.stereotype.Repository;
import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.service.schedule.CsvParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ConstraintRepository {
    private final Map<Long, GroupConstraint> constraints; // id -> GroupConstraint
    private final Map<Long, Long> constraintOwners; // constraintId -> userId
    private final AtomicLong nextId = new AtomicLong(1);

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
        constraints.put(constraint.id(), constraint);
        constraintOwners.put(constraint.id(), userId);
        return constraint;
    }

    public List<GroupConstraint> findByUserId(long userId) {
        return constraintOwners.entrySet().stream()
                .filter(e -> e.getValue() == userId)
                .map(e -> constraints.get(e.getKey()))
                .toList();
    }

    public GroupConstraint deleteById(long id) {
        GroupConstraint deletedGroupConstraint = constraints.remove(id);
        constraintOwners.remove(id);
        return deletedGroupConstraint;
    }

    public Optional<Long> getOwnerId(long id) {
        return Optional.ofNullable(constraintOwners.get(id));
    }

    public long getNextId() {
        return nextId.getAndIncrement();
    }
}
