package zhuangyan.timeplanning.view.renderers;

import zhuangyan.timeplanning.model.Task;

import javax.swing.*;
import java.awt.*;

public class TaskCardRenderer extends JPanel implements ListCellRenderer<Task> {
    private final JLabel nameLabel = new JLabel();
    private final JLabel infoLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();
    private final JPanel colorStrip;

    public TaskCardRenderer() {
        setLayout(new BorderLayout(5, 5));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        colorStrip = new JPanel();
        colorStrip.setPreferredSize(new Dimension(5, 60));
        add(colorStrip, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(0, 1, 2, 2));
        textPanel.setOpaque(false);
        textPanel.add(nameLabel);
        textPanel.add(infoLabel);
        textPanel.add(dateLabel);
        add(textPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        nameLabel.setText("<html><b>" + task.name() + "</b></html>");
        infoLabel.setText("⏱ " + task.duration().toMinutes() + " min  |  " + task.group());
        String earliest = task.timeWindow() != null ? task.timeWindow().earliestStart().toString() : "N/A";
        String latest = task.timeWindow() != null ? task.timeWindow().latestEnd().toString() : "N/A";
        dateLabel.setText("📅 " + earliest + " → " + latest);

        colorStrip.setBackground(ColorSelector.colorForGroup(task.group()));

        if (isSelected) {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLUE, 3),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
        } else {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        }
        return this;
    }
}
