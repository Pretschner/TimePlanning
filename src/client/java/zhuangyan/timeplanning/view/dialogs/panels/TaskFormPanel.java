package zhuangyan.timeplanning.view.dialogs.panels;

import javax.swing.*;
import java.awt.*;

/** Reusable form for task fields; optionally adds 7 weekday checkboxes for batch mode. */
public class TaskFormPanel extends JPanel {
    private final JTextField nameField;
    private final JTextField groupField;
    private final JTextField durationField;
    private final JTextField earliestField;
    private final JTextField latestField;

    private JCheckBox[] checkBoxes;

    public TaskFormPanel(boolean batch) {
        nameField = new JTextField();
        groupField = new JTextField();
        durationField = new JTextField();
        earliestField = new JTextField();
        latestField = new JTextField();

        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Group:"));
        formPanel.add(groupField);
        formPanel.add(new JLabel("Duration (min):"));
        formPanel.add(durationField);
        formPanel.add(new JLabel("Earliest Start " + (batch ? "(HH:MM):" : "(DDD, HH:MM)")));
        formPanel.add(earliestField);
        formPanel.add(new JLabel("Latest End " + (batch ? "(HH:MM):" : "(DDD, HH:MM)")));
        formPanel.add(latestField);

        add(formPanel, BorderLayout.CENTER);

        if (batch) {
            checkBoxes = new JCheckBox[]{new JCheckBox("MON"), new JCheckBox("TUE"), new JCheckBox("WED"), new JCheckBox("THU"), new JCheckBox("FRI"), new JCheckBox("SAT"), new JCheckBox("SUN")};

            JPanel checkBoxPanel = new JPanel(new GridLayout(2, 1, 8, 6));
            checkBoxPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JPanel rowWeekdays = new JPanel();
            rowWeekdays.add(new JLabel("Weekdays:"));
            JPanel rowWeekends = new JPanel();
            rowWeekends.add(new JLabel("Weekends:"));

            for (int i = 0; i < checkBoxes.length; i++) {
                if (i < 5) {
                    rowWeekdays.add(checkBoxes[i]);
                } else {
                    rowWeekends.add(checkBoxes[i]);
                }
            }

            checkBoxPanel.add(rowWeekdays);
            checkBoxPanel.add(rowWeekends);
            add(checkBoxPanel, BorderLayout.NORTH);
        }
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getGroupField() {
        return groupField;
    }

    public JTextField getDurationField() {
        return durationField;
    }

    public JTextField getEarliestField() {
        return earliestField;
    }

    public JTextField getLatestField() {
        return latestField;
    }

    public JCheckBox[] getCheckBoxes() {
        return checkBoxes;
    }
}
