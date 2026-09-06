package zhuangyan.timeplanning.view.dialogs.panels;

import javax.swing.*;
import java.awt.*;

/** Right-aligned Execute/Cancel buttons; label changes to "Generate" in batch mode. */
public class ButtonPanel extends JPanel {
    private final JButton execute;
    private final JButton cancel;

    public ButtonPanel(boolean batch) {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        execute = new JButton(batch ? "Generate" : "Add");
        cancel = new JButton("Cancel");
        add(execute);
        add(cancel);
    }

    public JButton getExecute() {
        return execute;
    }

    public JButton getCancel() {
        return cancel;
    }
}
