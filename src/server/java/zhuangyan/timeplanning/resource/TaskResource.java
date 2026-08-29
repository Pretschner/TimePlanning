package zhuangyan.timeplanning.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.service.AuthenticationService;
import zhuangyan.timeplanning.service.TaskService;
import java.util.List;

@RestController
public class TaskResource {

    private final TaskService taskService;
    private final AuthenticationService authenticationResource;

    @Autowired
    public TaskResource(TaskService taskService, AuthenticationService authenticationResource) {
        this.taskService = taskService;
        this.authenticationResource = authenticationResource;
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> createTask(@RequestBody Task task, @RequestHeader("Authorization") String authHeader) {
        if (task.id() != null && task.id() > 0) {
            return ResponseEntity.badRequest().build();
        }
        long userId = authenticationResource.getUserId(authHeader);
        Task res = taskService.createTask(task, userId);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@RequestBody Task task, @PathVariable long id, @RequestHeader("Authorization") String authHeader) {
        if (task.id() != id) {
            return ResponseEntity.badRequest().build();
        }
        long userId = authenticationResource.getUserId(authHeader);
        Task res = taskService.updateTask(task, id, userId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable long id, @RequestHeader("Authorization") String authHeader) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        long userId = authenticationResource.getUserId(authHeader);
        Task deletedTask = taskService.deleteTask(id, userId);
        return ResponseEntity.ok(deletedTask);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getTasks(@RequestHeader("Authorization") String authHeader) {
        long userId = authenticationResource.getUserId(authHeader);
        return ResponseEntity.ok(taskService.getTasks(userId));
    }
}
