package zhuangyan.timeplanning.view;

import zhuangyan.timeplanning.controller.ConstraintController;
import zhuangyan.timeplanning.controller.ScheduleController;
import zhuangyan.timeplanning.controller.TaskController;
import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.model.ScheduleConfig;
import zhuangyan.timeplanning.model.Task;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

public class ControllerCaller {
    // CONTROLLER CALLS
    private final TaskController taskController = new TaskController();
    private final ConstraintController constraintController = new ConstraintController();
    private final ScheduleController scheduleController = new ScheduleController();
    private final MainApplicationUI mainUI;
    
    public ControllerCaller(MainApplicationUI mainUI) {
        this.mainUI = mainUI;
    }

    public void callAddTask(Task task) {
        new SwingWorker<Void, Void>() {
            private Exception error;
            @Override protected Void doInBackground() {
                try { taskController.addTask(task, mainUI::updateTaskTable); }
                catch (Exception e) { error = e; }
                return null;
            }
            @Override protected void done() {
                if (error != null) showError("Failed to add task", error);
            }
        }.execute();
    }

    public void callUpdateTask(Task task) {
        new SwingWorker<Void, Void>() {
            private Exception error;
            @Override protected Void doInBackground() {
                try { taskController.editTask(task, mainUI::updateTaskTable); }
                catch (Exception e) { error = e; }
                return null;
            }
            @Override protected void done() {
                if (error != null) showError("Failed to update task", error);
            }
        }.execute();
    }

    public void deleteTask(Task task) {
        int confirm = JOptionPane.showConfirmDialog(mainUI, "Delete task '" + task.name() + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        new SwingWorker<Void, Void>() {
            private Exception error;
            @Override protected Void doInBackground() {
                try { taskController.deleteTask(task, mainUI::updateTaskTable); }
                catch (Exception e) { error = e; }
                return null;
            }
            @Override protected void done() {
                if (error != null) showError("Delete failed", error);
            }
        }.execute();
    }

    public void callAddConstraint(GroupConstraint c) {
        new SwingWorker<Void, Void>() {
            private Exception error;
            @Override protected Void doInBackground() {
                try { constraintController.addGroupConstraint(c, mainUI::updateConstraintTable); }
                catch (Exception e) { error = e; }
                return null;
            }
            @Override protected void done() {
                if (error != null) showError("Failed to add constraint", error);
            }
        }.execute();
    }

    public void callUpdateConstraint(GroupConstraint c) {
        new SwingWorker<Void, Void>() {
            private Exception error;
            @Override protected Void doInBackground() {
                try { constraintController.editGroupConstraint(c, mainUI::updateConstraintTable); }
                catch (Exception e) { error = e; }
                return null;
            }
            @Override protected void done() {
                if (error != null) showError("Failed to update constraint", error);
            }
        }.execute();
    }

    public void deleteConstraint(GroupConstraint c) {
        int confirm = JOptionPane.showConfirmDialog(mainUI, "Delete constraint (" + c.sourceGroup() + " -> " + c.targetGroup() + ")?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        new SwingWorker<Void, Void>() {
            private Exception error;
            @Override protected Void doInBackground() {
                try { constraintController.deleteGroupConstraint(c, mainUI::updateConstraintTable); }
                catch (Exception e) { error = e; }
                return null;
            }
            @Override protected void done() {
                if (error != null) showError("Delete failed", error);
            }
        }.execute();
    }

    // DATA SYNC ON STARTUP

    public void syncAllDataFromServer() {
        new SwingWorker<Void, Void>() {
            private Exception error;
            @Override protected Void doInBackground() {
                CountDownLatch latch = new CountDownLatch(3);
                try {
                    taskController.getAllTasks(tasks -> { mainUI.updateTaskTable(tasks); latch.countDown(); });
                    constraintController.getAllConstraints(constraints -> { mainUI.updateConstraintTable(constraints); latch.countDown(); });
                    scheduleController.getAllSchedules(schedules -> {
                        if (!schedules.isEmpty()) mainUI.updateTimetable(schedules);
                        latch.countDown();
                    });
                    latch.await();
                } catch (Exception e) { error = e; }
                return null;
            }
            @Override protected void done() {
                if (error != null) showError("Sync failed", error);
            }
        }.execute();
    }

    public void generateSchedule(ScheduleConfig config) {
        new SwingWorker<Void, Void>() {
            private Exception error;
            @Override protected Void doInBackground() {
                try { scheduleController.addSchedule(config, mainUI::updateTimetable); }
                catch (Exception e) { error = e; }
                return null;
            }
            @Override protected void done() {
                if (error != null) showError("Schedule generation failed", error);
                else JOptionPane.showMessageDialog(mainUI, "Schedule generated!");
            }
        }.execute();
    }

    // Error Handling
    private void showError(String message, Exception e) {
        SwingUtilities.invokeLater(() -> {
            String detail = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            JOptionPane.showMessageDialog(mainUI, message + ":\n" + detail, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
}
