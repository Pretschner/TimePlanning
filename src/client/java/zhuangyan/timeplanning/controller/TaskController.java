package zhuangyan.timeplanning.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import zhuangyan.timeplanning.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TaskController {

    private final RestClient restClient;
    private final List<Task> tasks;

    public TaskController() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:8080/")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        tasks = new ArrayList<>();
    }

    public void addTask(Task task, Consumer<List<Task>> tasksConsumer) {
        Task addedTask = restClient.post()
                .uri("tasks")
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .body(task)
                .retrieve()
                .toEntity(Task.class)
                .getBody();
        tasks.add(addedTask);
        tasksConsumer.accept(tasks);
    }

    public void editTask(Task task, Consumer<List<Task>> tasksConsumer) {
        Task updatedTask = restClient.put()
                .uri("tasks/" + task.id())
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .body(task)
                .retrieve()
                .toEntity(Task.class)
                .getBody();
        tasks.replaceAll(oldTask -> oldTask.id().equals(updatedTask.id()) ? updatedTask : oldTask);
        tasksConsumer.accept(tasks);
    }

    public void deleteTask(Task task, Consumer<List<Task>> tasksConsumer) {
        Task deletedTask = restClient.delete()
                .uri("tasks/" + task.id())
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .retrieve()
                .toEntity(Task.class)
                .getBody();
        tasks.removeIf(t -> t.id().equals(deletedTask.id()));
        tasksConsumer.accept(tasks);
    }

    public void getAllTasks(Consumer<List<Task>> tasksConsumer) {
        List<Task> receivedTasks = restClient.get()
                .uri("tasks")
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Task>>() {})
                .getBody();
        tasks.clear();
        tasks.addAll(receivedTasks);
        tasksConsumer.accept(tasks);
    }
}
