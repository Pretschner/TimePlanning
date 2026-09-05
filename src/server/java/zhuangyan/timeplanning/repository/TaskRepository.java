package zhuangyan.timeplanning.repository;

import org.springframework.stereotype.Repository;
import zhuangyan.timeplanning.model.Task;
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
public class TaskRepository {
    private final Map<Long, Task> tasks;
    private final Map<Long, Long> taskOwners;
    private final AtomicLong nextId = new AtomicLong(1);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public TaskRepository() {
        tasks = new ConcurrentHashMap<>();
        taskOwners = new ConcurrentHashMap<>();
        CsvParser parser = new CsvParser();
        try {
            List<Task> tasks = parser.readTasks(Path.of("tasks.csv"));
            for (var t : tasks) {
                save(t, 1);
                nextId.getAndIncrement();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Task save(Task task, long userId) {
        lock.writeLock().lock();
        try {
            tasks.put(task.id(), task);
            taskOwners.put(task.id(), userId);
            return task;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Task> findByUserId(long userId) {
        lock.readLock().lock();
        try {
            return taskOwners.entrySet().stream()
                    .filter(e -> e.getValue() == userId)
                    .map(e -> tasks.get(e.getKey()))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Task deleteById(long id) {
        lock.writeLock().lock();
        try {
            Task deletedTask = tasks.remove(id);
            taskOwners.remove(id);
            return deletedTask;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Long> getOwnerId(long id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(taskOwners.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    public long getNextId() {
        return nextId.getAndIncrement();
    }
}