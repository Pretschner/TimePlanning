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

@Repository
public class TaskRepository {
    private final Map<Long, Task> tasks; // id -> Task
    private final Map<Long, Long> taskOwners; // taskId -> userId
    private final AtomicLong nextId = new AtomicLong(1);

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
        tasks.put(task.id(), task);
        taskOwners.put(task.id(), userId);
        return task;
    }

    public List<Task> findByUserId(long userId) {
        return taskOwners.entrySet().stream()
                .filter(e -> e.getValue() == userId)
                .map(e -> tasks.get(e.getKey()))
                .toList();
    }

    public Task deleteById(long id) {
        Task deletedTask = tasks.remove(id);
        taskOwners.remove(id);
        return deletedTask;
    }

    public Optional<Long> getOwnerId(long id) {
        return Optional.ofNullable(taskOwners.get(id));
    }

    public long getNextId() {
        return nextId.getAndIncrement();
    }
}
