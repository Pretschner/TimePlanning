package zhuangyan.timeplanning.view.dialogs.panels;

import javax.swing.*;
import java.awt.*;

/** Reusable form for constraint fields; labels adapt for batch (comma-separated) vs single. */
public class ConstraintFormPanel extends JPanel {
    private final JTextField srcField;
    private final JTextField trgField;
    private final JTextField minField;
    private final JTextField maxField;

    public ConstraintFormPanel(boolean batch) {
        setLayout(new GridLayout(0, 2, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        srcField = new JTextField();
        trgField = new JTextField();
        minField = new JTextField();
        maxField = new JTextField();

        add(new JLabel("Source Group" + (batch ? " (1st, 2nd...):" : ":")));
        add(srcField);
        add(new JLabel("Target Group" + (batch ? " (1st, 2nd...):" : ":")));
        add(trgField);
        add(new JLabel("Min. Gap (min):"));
        add(minField);
        add(new JLabel("Max. Gap (min):"));
        add(maxField);
    }

    public JTextField getSourceField() {
        return srcField;
    }

    public JTextField getTargetField() {
        return trgField;
    }

    public JTextField getMinField() {
        return minField;
    }

    public JTextField getMaxField() {
        return maxField;
    }
}
