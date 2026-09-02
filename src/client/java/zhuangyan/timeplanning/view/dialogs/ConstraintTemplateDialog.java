package zhuangyan.timeplanning.view.dialogs;

import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.view.MainApplicationUI;
import zhuangyan.timeplanning.view.dialogs.formpanels.ConstraintFormPanel;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConstraintTemplateDialog {
    public static void show(MainApplicationUI parent, Consumer<GroupConstraint> constraintConsumer) {
        JDialog dialog = new JDialog(parent, "Generate from Template", true);

        ConstraintFormPanel formPanel = new ConstraintFormPanel(true);

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
            String[] srcGroup = formPanel.getSourceField().getText().trim().split(", ");
            String[] trgGroup = formPanel.getTargetField().getText().trim().split(", ");
            String minGap = formPanel.getMinField().getText().trim();
            String maxGap = formPanel.getMaxField().getText().trim();

            if (srcGroup.length == 0) {
                JOptionPane.showMessageDialog(dialog, "Source Group is required.");
                formPanel.getSourceField().requestFocus();
                return;
            }
            if (trgGroup.length == 0) {
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
