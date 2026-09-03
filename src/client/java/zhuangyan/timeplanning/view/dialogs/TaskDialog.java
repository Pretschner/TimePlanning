package zhuangyan.timeplanning.view.dialogs;

import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.model.TimePoint;
import zhuangyan.timeplanning.model.TimeWindow;
import zhuangyan.timeplanning.view.MainApplicationUI;
import zhuangyan.timeplanning.view.dialogs.panels.ButtonPanel;
import zhuangyan.timeplanning.view.dialogs.panels.TaskFormPanel;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Consumer;

public class TaskDialog {
    public static void show(MainApplicationUI parent, Task existing, Consumer<Task> taskConsumer) {
        boolean isNew = existing == null;
        JDialog dialog = new JDialog(parent, isNew ? "Add New Task" : "Update Task", true);

        TaskFormPanel formPanel = new TaskFormPanel(false);

        if (!isNew) {
            formPanel.getNameField().setText(existing.name());
            formPanel.getGroupField().setText(existing.group());
            formPanel.getDurationField().setText("" + existing.duration().toMinutes());
            formPanel.getEarliestField().setText(existing.timeWindow().earliestStart().toString());
            formPanel.getLatestField().setText(existing.timeWindow().latestEnd().toString());
        }

        ButtonPanel buttonPanel = new ButtonPanel(false);

        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.getRootPane().setDefaultButton(buttonPanel.getExecute());

        final Task[] result = {null};

        buttonPanel.getExecute().addActionListener(e -> {
            String name = formPanel.getNameField().getText().trim();
            String durStr = formPanel.getDurationField().getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name is required.");
                formPanel.getNameField().requestFocus();
                return;
            }
            if (durStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Duration is required.");
                formPanel.getDurationField().requestFocus();
                return;
            }

            try {
                // Parsing Duration
                int mins = Integer.parseInt(durStr);

                // Parsing TimeWindow
                TimePoint start = parseTimePoint(formPanel.getEarliestField()), end = parseTimePoint(formPanel.getLatestField());
                TimeWindow tw = new TimeWindow(start, end);

                // Assembling Result
                result[0] = new Task(existing != null ? existing.id() : null, name, formPanel.getGroupField().getText().trim(),
                        Duration.ofMinutes(mins), tw);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Duration must be a number.");
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Time format: DDD, HH:MM (e.g. THU, 09:00)");
            }
        });

        buttonPanel.getCancel().addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);

        if (result[0] != null) {
            taskConsumer.accept(result[0]);
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
        // Standard Values for Unfilled TextFields
        return new TimePoint(DayOfWeek.MONDAY, LocalTime.MIN);
    }
}
