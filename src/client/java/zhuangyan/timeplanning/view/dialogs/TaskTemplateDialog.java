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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Batch-create tasks for checked weekdays using a shared name/duration/time window. */
public class TaskTemplateDialog extends BaseDialog<Task> {
    private final DayOfWeek[] days = {
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    };

    public TaskTemplateDialog(MainApplicationUI parent, Consumer<Task> taskConsumer) {
        super(parent, "Generate Tasks from Template", true, new TaskFormPanel(true), taskConsumer);
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
            List<Task> result = new ArrayList<>();

            JCheckBox[] checkBoxes = fp.getCheckBoxes();
            for (int i = 0; i < 7; i++) {
                if (checkBoxes[i] != null && checkBoxes[i].isSelected()) {
                    TimeWindow tw = parseTimeWindow(fp.getEarliestField(), fp.getLatestField(), days[i]);
                    result.add(new Task(null, name, fp.getGroupField().getText().trim(), Duration.ofMinutes(mins), tw));
                }
            }

            closeWithResults(result);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Duration must be a number.");
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(dialog, "Time format: HH:MM (e.g. 09:00)");
        }
    }

    private static TimeWindow parseTimeWindow(JTextField earliestField, JTextField latestField, DayOfWeek day) {
        TimePoint start, end;
        LocalTime startTime, endTime;

        if (!earliestField.getText().trim().isEmpty()) {
            String time = earliestField.getText().trim();
            startTime = LocalTime.parse(time);
            start = new TimePoint(day, startTime);
        } else {
            startTime = LocalTime.MIN;
            start = new TimePoint(day, startTime);
        }

        if (!latestField.getText().trim().isEmpty()) {
            String time = latestField.getText().trim();
            endTime = LocalTime.parse(time);
            end = new TimePoint(!endTime.isBefore(startTime) ? day : DayOfWeek.of((day.getValue() % 7) + 1), endTime);
        } else {
            endTime = LocalTime.MIN;
            end = new TimePoint(!endTime.isBefore(startTime) ? day : DayOfWeek.of((day.getValue() % 7) + 1), endTime);
        }

        return new TimeWindow(start, end);
    }

    public static void show(MainApplicationUI parent, Consumer<Task> taskConsumer) {
        new TaskTemplateDialog(parent, taskConsumer).show();
    }
}