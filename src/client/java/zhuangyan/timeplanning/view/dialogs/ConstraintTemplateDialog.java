package zhuangyan.timeplanning.view.dialogs;

import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.view.MainApplicationUI;
import zhuangyan.timeplanning.view.dialogs.panels.ConstraintFormPanel;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/** Batch-create constraints from comma-separated source/target group lists (cartesian product). */
public class ConstraintTemplateDialog extends BaseDialog<GroupConstraint> {

    public ConstraintTemplateDialog(MainApplicationUI parent, Consumer<GroupConstraint> constraintConsumer) {
        super(parent, "Generate from Template", true, new ConstraintFormPanel(true), constraintConsumer);
    }

    @Override
    protected void onExecute() {
        ConstraintFormPanel fp = (ConstraintFormPanel) formPanel;
        String[] srcGroup = fp.getSourceField().getText().trim().split(", ");
        String[] trgGroup = fp.getTargetField().getText().trim().split(", ");
        String minGap = fp.getMinField().getText().trim();
        String maxGap = fp.getMaxField().getText().trim();

        if (Arrays.stream(srcGroup).anyMatch(String::isEmpty)) {
            JOptionPane.showMessageDialog(dialog, "Source Group is required.");
            fp.getSourceField().requestFocus();
            return;
        }
        if (Arrays.stream(trgGroup).anyMatch(String::isEmpty)) {
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
            List<GroupConstraint> result = new ArrayList<>();
            for (int i = 0; i < srcGroup.length; i++) {
                for (int j = 0; j < trgGroup.length; j++) {
                    result.add(new GroupConstraint(null, srcGroup[i], trgGroup[j], minimumGap, maximumGap));
                }
            }
            closeWithResults(result);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Duration must be a number.");
        }
    }

    public static void show(MainApplicationUI parent, Consumer<GroupConstraint> constraintConsumer) {
        new ConstraintTemplateDialog(parent, constraintConsumer).show();
    }
}