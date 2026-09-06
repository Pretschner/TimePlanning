package zhuangyan.timeplanning.view.dialogs;

import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.view.MainApplicationUI;
import zhuangyan.timeplanning.view.dialogs.panels.ConstraintFormPanel;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.function.Consumer;

public class ConstraintDialog extends BaseDialog<GroupConstraint> {
    private final GroupConstraint existing;

    public ConstraintDialog(MainApplicationUI parent, GroupConstraint existing, Consumer<GroupConstraint> constraintConsumer) {
        super(parent, existing == null ? "Add New Constraint" : "Update Constraint", false, new ConstraintFormPanel(false), constraintConsumer);
        this.existing = existing;

        if (existing != null) {
            ConstraintFormPanel fp = (ConstraintFormPanel) formPanel;
            fp.getSourceField().setText(existing.sourceGroup());
            fp.getTargetField().setText(existing.targetGroup());
            fp.getMinField().setText(existing.minimumGap() != null ? "" + existing.minimumGap().toMinutes() : "");
            fp.getMaxField().setText(existing.maximumGap() != null ? "" + existing.maximumGap().toMinutes() : "");
        }
    }

    @Override
    protected void onExecute() {
        ConstraintFormPanel fp = (ConstraintFormPanel) formPanel;
        String srcGroup = fp.getSourceField().getText().trim();
        String trgGroup = fp.getTargetField().getText().trim();
        String minGap = fp.getMinField().getText().trim();
        String maxGap = fp.getMaxField().getText().trim();

        if (srcGroup.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Source Group is required.");
            fp.getSourceField().requestFocus();
            return;
        }
        if (trgGroup.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Target Group is required.");
            fp.getTargetField().requestFocus();
            return;
        }
        if (minGap.isEmpty() && maxGap.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Minimum or Maximum Gap is required.");
            fp.getMinField().requestFocus();
            return;
        }

        try {
            Duration minimumGap = minGap.isEmpty() ? null : Duration.ofMinutes(Integer.parseInt(minGap));
            Duration maximumGap = maxGap.isEmpty() ? null : Duration.ofMinutes(Integer.parseInt(maxGap));
            GroupConstraint result = new GroupConstraint(existing != null ? existing.id() : null, srcGroup, trgGroup, minimumGap, maximumGap);
            closeWithResult(result);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Duration must be a number.");
        }
    }

    public static void show(MainApplicationUI parent, GroupConstraint existing, Consumer<GroupConstraint> constraintConsumer) {
        new ConstraintDialog(parent, existing, constraintConsumer).show();
    }
}