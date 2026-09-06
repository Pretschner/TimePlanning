package zhuangyan.timeplanning.controller;

import zhuangyan.timeplanning.model.Task;

public class TaskController extends CrudController<Task> {
    public TaskController() {
        super("tasks", Task.class, Task::id);
    }
}