package zhuangyan.timeplanning.view.dialogs;

import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.view.MainApplicationUI;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConstraintTemplateDialog {
    public static void show(MainApplicationUI parent, Consumer<GroupConstraint> constraintConsumer) {
        JDialog dialog = new JDialog(parent, "Generate from Template", true);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        final JTextField srcField = new JTextField();
        final JTextField trgField = new JTextField();
        final JTextField minField = new JTextField();
        final JTextField maxField = new JTextField();

        formPanel.add(new JLabel("Source Group (1st, 2nd, ...):"));
        formPanel.add(srcField);
        formPanel.add(new JLabel("Target Group (1st, 2nd, ...):"));
        formPanel.add(trgField);
        formPanel.add(new JLabel("Min. Gap (min):"));
        formPanel.add(minField);
        formPanel.add(new JLabel("Max. Gap (min):"));
        formPanel.add(maxField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton execute = new JButton("Generate");
        JButton cancel = new JButton("Cancel");
        buttonPanel.add(execute);
        buttonPanel.add(cancel);

        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.getRootPane().setDefaultButton(execute);

        final List<GroupConstraint> result = new ArrayList<>();

        execute.addActionListener(e -> {
            String[] srcGroup = srcField.getText().trim().split(", ");
            String[] trgGroup = trgField.getText().trim().split(", ");
            String minGap = minField.getText().trim();
            String maxGap = maxField.getText().trim();

            if (srcGroup.length == 0) {
                JOptionPane.showMessageDialog(dialog, "Source Group is required.");
                srcField.requestFocus();
                return;
            }
            if (trgGroup.length == 0) {
                JOptionPane.showMessageDialog(dialog, "Target Group is required.");
                trgField.requestFocus();
                return;
            }
            if (minGap.isEmpty() && maxGap.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Minimum or Maximum Gap is required.");
                minField.requestFocus();
                return;
            }

            try {
                Duration minimumGap = minGap.isEmpty() ? null : Duration.ofMinutes(Integer.parseInt(minGap));
                Duration maximumGap = maxGap.isEmpty() ? null : Duration.ofMinutes(Integer.parseInt(maxGap));
                for (int i = 0; i < srcGroup.length; i++) {
                    for (int j = 0; j < trgGroup.length; j++) {
                        result.add(new GroupConstraint(null, srcGroup[i], trgGroup[j], minimumGap, maximumGap));
                    }
                }
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Duration must be a number.");
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);

        for (var c : result) {
            constraintConsumer.accept(c);
        }
    }
}
