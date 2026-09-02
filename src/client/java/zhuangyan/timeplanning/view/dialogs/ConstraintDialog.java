package zhuangyan.timeplanning.view.dialogs;

import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.view.MainApplicationUI;
import zhuangyan.timeplanning.view.dialogs.formpanels.ConstraintFormPanel;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.function.Consumer;

public class ConstraintDialog {
    public static void show(MainApplicationUI parent, GroupConstraint existing, Consumer<GroupConstraint> constraintConsumer) {
        boolean isNew = existing == null;
        JDialog dialog = new JDialog(parent, isNew ? "Add New Constraint" : "Update Constraint", true);

        ConstraintFormPanel formPanel = new ConstraintFormPanel(false);

        if (!isNew) {
            formPanel.getSourceField().setText(existing.sourceGroup());
            formPanel.getTargetField().setText(existing.targetGroup());
            formPanel.getMinField().setText(existing.minimumGap() != null ? "" + existing.minimumGap().toMinutes() : "");
            formPanel.getMaxField().setText(existing.maximumGap() != null ? "" + existing.maximumGap().toMinutes() : "");
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton execute = new JButton(isNew ? "Add" : "Update");
        JButton cancel = new JButton("Cancel");
        buttonPanel.add(execute);
        buttonPanel.add(cancel);

        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.getRootPane().setDefaultButton(execute);

        final GroupConstraint[] result = {null};

        execute.addActionListener(e -> {
            String srcGroup = formPanel.getSourceField().getText().trim();
            String trgGroup = formPanel.getTargetField().getText().trim();
            String minGap = formPanel.getMinField().getText().trim();
            String maxGap = formPanel.getMaxField().getText().trim();

            if (srcGroup.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Source Group is required.");
                formPanel.getSourceField().requestFocus();
                return;
            }
            if (trgGroup.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Target Group is required.");
                formPanel.getTargetField().requestFocus();
                return;
            }
            if (minGap.isEmpty() && maxGap.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Minimum or Maximum Gap is required.");
                formPanel.getMinField().requestFocus();
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

        cancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);

        if (result[0] != null) {
            constraintConsumer.accept(result[0]);
        }
    }
}
