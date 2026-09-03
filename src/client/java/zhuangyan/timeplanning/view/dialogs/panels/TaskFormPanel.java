package zhuangyan.timeplanning.view.dialogs.panels;

import javax.swing.*;
import java.awt.*;

public class TaskFormPanel extends JPanel {
    private final JTextField nameField;
    private final JTextField groupField;
    private final JTextField durationField;
    private final JTextField earliestField;
    private final JTextField latestField;

    public TaskFormPanel(boolean batch) {
        setLayout(new GridLayout(0, 2, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        nameField = new JTextField();
        groupField = new JTextField();
        durationField = new JTextField();
        earliestField = new JTextField();
        latestField = new JTextField();

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Group:"));
        add(groupField);
        add(new JLabel("Duration (min):"));
        add(durationField);
        add(new JLabel("Earliest Start " + (batch ? "(HH:MM):" : "(DDD, HH:MM)")));
        add(earliestField);
        add(new JLabel("Latest End " + (batch ? "(HH:MM):" : "(DDD, HH:MM)")));
        add(latestField);
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
}
