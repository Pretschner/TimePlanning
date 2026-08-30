package zhuangyan.timeplanning.view.renderers;

import zhuangyan.timeplanning.model.GroupConstraint;

import javax.swing.*;
import java.awt.*;

public class ConstraintCardRenderer extends JPanel implements ListCellRenderer<GroupConstraint> {
    private final JLabel titleLabel = new JLabel();
    private final JLabel gapLabel = new JLabel();
    private final JPanel colorStripLeft, colorStripRight;

    public ConstraintCardRenderer() {
        setLayout(new BorderLayout(5, 5));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        colorStripLeft = new JPanel();
        colorStripLeft.setPreferredSize(new Dimension(5, 50));
        add(colorStripLeft, BorderLayout.WEST);
        colorStripRight = new JPanel();
        colorStripRight.setPreferredSize(new Dimension(5, 50));
        add(colorStripRight, BorderLayout.EAST);

        JPanel textPanel = new JPanel(new GridLayout(0, 1, 2, 2));
        textPanel.setOpaque(false);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        textPanel.add(titleLabel);
        textPanel.add(gapLabel);
        add(textPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends GroupConstraint> list,
                                                  GroupConstraint c, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        titleLabel.setText(c.sourceGroup() + " → " + c.targetGroup());
        gapLabel.setText("⏳ Min: " + (c.minimumGap() != null ? c.minimumGap().toMinutes() + "m" : "N/A") +  " |  Max: " + (c.maximumGap() != null ? c.maximumGap().toMinutes() + "m" : "N/A"));

        colorStripLeft.setBackground(ColorSelector.colorForGroup(c.sourceGroup()));
        colorStripRight.setBackground(ColorSelector.colorForGroup(c.targetGroup()));

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
