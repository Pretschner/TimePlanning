package zhuangyan.timeplanning.controller;

import zhuangyan.timeplanning.model.Task;

/** Tiny subclass pointing {@code CrudController} at {@code /tasks} with {@code Task}/{@code Long}. */
public class TaskController extends CrudController<Task> {
    public TaskController() {
        super("tasks", Task.class, Task::id);
    }
}