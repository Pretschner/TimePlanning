package zhuangyan.timeplanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.exception.ForbiddenException;
import zhuangyan.timeplanning.exception.NotFoundException;
import zhuangyan.timeplanning.repository.TaskRepository;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task, long userId) {
        Task newTask = new Task(taskRepository.getNextId(), task.name(), task.group(), task.duration(), task.timeWindow());
        return taskRepository.save(newTask, userId);
    }

    public Task updateTask(Task task, long id, long userId) {
        validateOwnership(id, userId);
        Task updatedTask = new Task(id, task.name(), task.group(), task.duration(), task.timeWindow());
        return taskRepository.save(updatedTask, userId);
    }

    public Task deleteTask(long id, long userId) {
        validateOwnership(id, userId);
        return taskRepository.deleteById(id);
    }


    public List<Task> getTasks(long userId) {
        return taskRepository.findByUserId(userId);
    }

    private void validateOwnership(long taskId, long userId) {
        long ownerId = taskRepository.getOwnerId(taskId)
                .orElseThrow(() -> new NotFoundException("The task queried does not exist."));
        if (ownerId != userId) {
            throw new ForbiddenException("The task queried does not belong to the user.");
        }
    }
}
