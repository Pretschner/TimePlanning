package zhuangyan.timeplanning.view.dialogs;

import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.model.TimePoint;
import zhuangyan.timeplanning.model.TimeWindow;
import zhuangyan.timeplanning.view.MainApplicationUI;
import zhuangyan.timeplanning.view.dialogs.panels.TaskFormPanel;

import javax.swing.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Consumer;

public class TaskDialog extends BaseDialog<Task> {
    private final Task existing;

    public TaskDialog(MainApplicationUI parent, Task existing, Consumer<Task> taskConsumer) {
        super(parent, existing == null ? "Add New Task" : "Update Task", false, new TaskFormPanel(false), taskConsumer);
        this.existing = existing;

        if (existing != null) {
            TaskFormPanel fp = (TaskFormPanel) formPanel;
            fp.getNameField().setText(existing.name());
            fp.getGroupField().setText(existing.group());
            fp.getDurationField().setText("" + existing.duration().toMinutes());
            fp.getEarliestField().setText(existing.timeWindow().earliestStart().toString());
            fp.getLatestField().setText(existing.timeWindow().latestEnd().toString());
        }
    }

    @Override
    protected void onExecute() {
        TaskFormPanel fp = (TaskFormPanel) formPanel;
        String name = fp.getNameField().getText().trim();
        String durStr = fp.getDurationField().getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Name is required.");
            fp.getNameField().requestFocus();
            return;
        }
        if (durStr.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Duration is required.");
            fp.getDurationField().requestFocus();
            return;
        }

        try {
            int mins = Integer.parseInt(durStr);
            TimePoint start = parseTimePoint(fp.getEarliestField());
            TimePoint end = parseTimePoint(fp.getLatestField());
            TimeWindow tw = new TimeWindow(start, end);

            Task result = new Task(existing != null ? existing.id() : null, name, fp.getGroupField().getText().trim(),
                    Duration.ofMinutes(mins), tw);
            closeWithResult(result);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Duration must be a number.");
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(dialog, "Time format: DDD, HH:MM (e.g. THU, 09:00)");
        }
    }

    private static TimePoint parseTimePoint(JTextField textField) {
        if (!textField.getText().trim().isEmpty()) {
            String[] dayAndTime = textField.getText().trim().split(", ");
            DayOfWeek day = switch (dayAndTime[0]) {
                case "MON" -> DayOfWeek.MONDAY;
                case "TUE" -> DayOfWeek.TUESDAY;
                case "WED" -> DayOfWeek.WEDNESDAY;
                case "THU" -> DayOfWeek.THURSDAY;
                case "FRI" -> DayOfWeek.FRIDAY;
                case "SAT" -> DayOfWeek.SATURDAY;
                case "SUN" -> DayOfWeek.SUNDAY;
                default -> throw new IllegalStateException("Unexpected value: " + dayAndTime[0]);
            };
            return new TimePoint(day, LocalTime.parse(dayAndTime[1]));
        }
        return new TimePoint(DayOfWeek.MONDAY, LocalTime.MIN);
    }

    public static void show(MainApplicationUI parent, Task existing, Consumer<Task> taskConsumer) {
        new TaskDialog(parent, existing, taskConsumer).show();
    }
}