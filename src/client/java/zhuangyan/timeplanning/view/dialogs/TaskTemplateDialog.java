package zhuangyan.timeplanning.view.dialogs;

import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.model.TimePoint;
import zhuangyan.timeplanning.model.TimeWindow;
import zhuangyan.timeplanning.view.MainApplicationUI;
import zhuangyan.timeplanning.view.dialogs.formpanels.TaskFormPanel;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TaskTemplateDialog {
    public static void show(MainApplicationUI parent, Consumer<Task> taskConsumer) {
        JDialog dialog = new JDialog(parent, "Generate Tasks from Template", true);

        JCheckBox mondayCheck = new JCheckBox("MON");
        JCheckBox tuesdayCheck = new JCheckBox("TUE");
        JCheckBox wednesdayCheck = new JCheckBox("WED");
        JCheckBox thursdayCheck = new JCheckBox("THU");
        JCheckBox fridayCheck = new JCheckBox("FRI");
        JCheckBox saturdayCheck = new JCheckBox("SAT");
        JCheckBox sundayCheck = new JCheckBox("SUN");

        JPanel checkBoxPanel = new JPanel(new GridLayout(2, 1, 8, 6));
        checkBoxPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel rowWeekdays = new JPanel();
        rowWeekdays.add(new JLabel("Weekdays:"));
        rowWeekdays.add(mondayCheck);
        rowWeekdays.add(tuesdayCheck);
        rowWeekdays.add(wednesdayCheck);
        rowWeekdays.add(thursdayCheck);
        rowWeekdays.add(fridayCheck);

        JPanel rowWeekends = new JPanel();
        rowWeekends.add(new JLabel("Weekends:"));
        rowWeekends.add(saturdayCheck);
        rowWeekends.add(sundayCheck);

        checkBoxPanel.add(rowWeekdays);
        checkBoxPanel.add(rowWeekends);

        TaskFormPanel formPanel = new TaskFormPanel(true);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton execute = new JButton("Generate");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(execute);
        buttonPanel.add(cancelBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(checkBoxPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.getRootPane().setDefaultButton(execute);

        final List<Task> result = new ArrayList<>();

        execute.addActionListener(e -> {
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
                int mins = Integer.parseInt(durStr);
                if (mondayCheck.isSelected()) {
                    TimeWindow tw = parseTimeWindow(formPanel.getEarliestField(), formPanel.getLatestField(), DayOfWeek.MONDAY);
                    result.add(new Task(null, name, formPanel.getGroupField().getText().trim(), Duration.ofMinutes(mins), tw));
                }
                if (tuesdayCheck.isSelected()) {
                    TimeWindow tw = parseTimeWindow(formPanel.getEarliestField(), formPanel.getLatestField(), DayOfWeek.TUESDAY);
                    result.add(new Task(null, name, formPanel.getGroupField().getText().trim(), Duration.ofMinutes(mins), tw));
                }
                if (wednesdayCheck.isSelected()) {
                    TimeWindow tw = parseTimeWindow(formPanel.getEarliestField(), formPanel.getLatestField(), DayOfWeek.WEDNESDAY);
                    result.add(new Task(null, name, formPanel.getGroupField().getText().trim(), Duration.ofMinutes(mins), tw));
                }
                if (thursdayCheck.isSelected()) {
                    TimeWindow tw = parseTimeWindow(formPanel.getEarliestField(), formPanel.getLatestField(), DayOfWeek.THURSDAY);
                    result.add(new Task(null, name, formPanel.getGroupField().getText().trim(), Duration.ofMinutes(mins), tw));
                }
                if (fridayCheck.isSelected()) {
                    TimeWindow tw = parseTimeWindow(formPanel.getEarliestField(), formPanel.getLatestField(), DayOfWeek.FRIDAY);
                    result.add(new Task(null, name, formPanel.getGroupField().getText().trim(), Duration.ofMinutes(mins), tw));
                }
                if (saturdayCheck.isSelected()) {
                    TimeWindow tw = parseTimeWindow(formPanel.getEarliestField(), formPanel.getLatestField(), DayOfWeek.SATURDAY);
                    result.add(new Task(null, name, formPanel.getGroupField().getText().trim(), Duration.ofMinutes(mins), tw));
                }
                if (sundayCheck.isSelected()) {
                    TimeWindow tw = parseTimeWindow(formPanel.getEarliestField(), formPanel.getLatestField(), DayOfWeek.SUNDAY);
                    result.add(new Task(null, name, formPanel.getGroupField().getText().trim(), Duration.ofMinutes(mins), tw));
                }
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Duration must be a number.");
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Time format: HH:MM (e.g. 09:00)");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);

        for (var t : result) {
            taskConsumer.accept(t);
        }
    }

    private static TimeWindow parseTimeWindow(JTextField earliestField, JTextField latestField, DayOfWeek day) {
        TimePoint start, end;
        LocalTime startTime, endTime;

        if (!earliestField.getText().trim().isEmpty()) {
            String time = earliestField.getText().trim();
            startTime = LocalTime.parse(time);
            start = new TimePoint(day, startTime);
        }
        else {
            startTime = LocalTime.MIN;
            start = new TimePoint(day, startTime);
        }

        if (!latestField.getText().trim().isEmpty()) {
            String time = latestField.getText().trim();
            endTime = LocalTime.parse(time);
            end = new TimePoint(!endTime.isBefore(startTime) ? day : DayOfWeek.of((day.getValue() + 1) % 7), endTime);
        }
        else {
            endTime = LocalTime.MIN;
            end = new TimePoint(!endTime.isBefore(startTime) ? day : DayOfWeek.of((day.getValue() + 1) % 7), endTime);
        }

        return new TimeWindow(start, end);
    }
}
