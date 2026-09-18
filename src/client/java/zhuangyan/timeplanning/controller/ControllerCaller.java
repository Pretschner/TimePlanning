package zhuangyan.timeplanning.controller;

import org.springframework.core.ParameterizedTypeReference;
import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.model.ScheduleConfig;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.view.MainApplicationUI;

import javax.swing.*;
import java.util.List;

/** Bridges UI to controllers: runs network calls in SwingWorker (w. errors/confirmations), and routes callbacks back to the UI. */
public class ControllerCaller {
    private final TaskController taskController = new TaskController();
    private final ConstraintController constraintController = new ConstraintController();
    private final ScheduleController scheduleController = new ScheduleController();
    private final MainApplicationUI mainUI;
    
    public ControllerCaller(MainApplicationUI mainUI) {
        this.mainUI = mainUI;
    }

    public void callAddTask(Task task) {
        async(() -> taskController.add(task, mainUI::updateTaskTable));
    }

    public void callUpdateTask(Task task) {
        async(() -> taskController.edit(task, mainUI::updateTaskTable));
    }

    public void deleteTask(Task task) {
        confirm("Delete task '" + task.name() + "'?", () -> async(() -> taskController.delete(task, mainUI::updateTaskTable)));
    }

    public void callAddConstraint(GroupConstraint c) {
        async(() -> constraintController.add(c, mainUI::updateConstraintTable));
    }

    public void callUpdateConstraint(GroupConstraint c) {
        async(() -> constraintController.edit(c, mainUI::updateConstraintTable));
    }

    public void deleteConstraint(GroupConstraint c) {
        confirm("Delete constraint (" + c.sourceGroup() + " -> " + c.targetGroup() + ")?", () -> async(() -> constraintController.delete(c, mainUI::updateConstraintTable)));
    }

    public void generateSchedule(ScheduleConfig config) {
        async(() -> scheduleController.addSchedule(config, mainUI::updateTimetable));
    }

    // DATA SYNC ON STARTUP

    public void syncAllDataFromServer() {
        async(() -> {
            taskController.getAll(mainUI::updateTaskTable, new ParameterizedTypeReference<List<Task>>() {});
            constraintController.getAll(mainUI::updateConstraintTable, new ParameterizedTypeReference<List<GroupConstraint>>() {});
            scheduleController.getAllSchedules(mainUI::updateTimetable);
        });
    }

    private void async(Runnable task) {
        new SwingWorker<Void, Void>() {
            private Exception error;
            @Override protected Void doInBackground() {
                try {
                    task.run();
                } catch (Exception e) {
                    error = e;
                }
                return null;
            }
            @Override protected void done() {
                if (error != null) showError(error.getMessage(), error);
            }
        }.execute();
    }

    private void confirm(String message, Runnable onConfirm) {
        if (JOptionPane.showConfirmDialog(mainUI, message, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            onConfirm.run();
        }
    }

    // Error Handling
    private void showError(String message, Exception e) {
        SwingUtilities.invokeLater(() -> {
            String detail = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            JOptionPane.showMessageDialog(mainUI, message + ":\n" + detail, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
}
