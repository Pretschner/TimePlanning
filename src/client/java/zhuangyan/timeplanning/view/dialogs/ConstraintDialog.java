package zhuangyan.timeplanning.view.dialogs;

import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.view.MainApplicationUI;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.function.Consumer;

public class ConstraintDialog {
    public static void show(MainApplicationUI parent, GroupConstraint existing, Consumer<GroupConstraint> existingConsumer) {
        boolean isNew = existing == null;
        JDialog dialog = new JDialog(parent, isNew ? "Add New Constraint" : "Update Constraint", true);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        final JTextField sourceField = new JTextField(12);
        final JTextField targetField = new JTextField(12);
        final JTextField minField = new JTextField(12);
        final JTextField maxField = new JTextField(12);

        formPanel.add(new JLabel("Source Group:"));
        formPanel.add(sourceField);
        formPanel.add(new JLabel("Target Group:"));
        formPanel.add(targetField);
        formPanel.add(new JLabel("Minimum Gap (min):"));
        formPanel.add(minField);
        formPanel.add(new JLabel("Maximum Gap (min):"));
        formPanel.add(maxField);

        if (!isNew) {
            sourceField.setText(existing.sourceGroup());
            targetField.setText(existing.targetGroup());
            minField.setText(existing.minimumGap() != null ? "" + existing.minimumGap().toMinutes() : "");
            maxField.setText(existing.maximumGap() != null ? "" + existing.maximumGap().toMinutes() : "");
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton execute = new JButton(isNew ? "Add" : "Update");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(execute);
        buttonPanel.add(cancelBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.getRootPane().setDefaultButton(execute);

        final GroupConstraint[] result = {null};

        execute.addActionListener(e -> {
            String srcGroup = sourceField.getText().trim();
            String trgGroup = targetField.getText().trim();
            String minGap = minField.getText().trim();
            String maxGap = maxField.getText().trim();

            if (srcGroup.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Source Group is required.");
                sourceField.requestFocus();
                return;
            }
            if (trgGroup.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Target Group is required.");
                targetField.requestFocus();
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
                result[0] = new GroupConstraint(existing != null ? existing.id() : null, srcGroup, trgGroup, minimumGap, maximumGap);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Duration must be a number.");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);

        if (result[0] != null) {
            existingConsumer.accept(result[0]);
        }
    }
}
