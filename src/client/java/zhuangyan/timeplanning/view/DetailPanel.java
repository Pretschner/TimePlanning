package zhuangyan.timeplanning.view;

import zhuangyan.timeplanning.controller.ControllerCaller;
import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.model.TaskPlacement;
import zhuangyan.timeplanning.view.dialogs.ConstraintDialog;
import zhuangyan.timeplanning.view.dialogs.TaskDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DetailPanel extends JPanel {
    private final MainApplicationUI parent;
    private final ControllerCaller caller;
    private final CardLayout cardLayout;
    private static final String PLACEHOLDER = "--Placeholder--";

    private final JLabel taskName = new JLabel(PLACEHOLDER);
    private final JLabel taskGroup = new JLabel(PLACEHOLDER);
    private final JLabel taskDuration = new JLabel(PLACEHOLDER);
    private final JLabel taskEarliest = new JLabel(PLACEHOLDER);
    private final JLabel taskLatest = new JLabel(PLACEHOLDER);
    private final JButton taskEdit = new JButton("Edit");
    private final JButton taskDelete = new JButton("Delete");
    private ActionListener taskEditListener = e -> {};
    private ActionListener taskDeleteListener = e -> {};
    // Constraint
    private final JLabel constraintSource = new JLabel(PLACEHOLDER);
    private final JLabel constraintTarget = new JLabel(PLACEHOLDER);
    private final JLabel constraintMin = new JLabel(PLACEHOLDER);
    private final JLabel constraintMax = new JLabel(PLACEHOLDER);
    private final JButton constraintEdit = new JButton("Edit");
    private final JButton constraintDelete = new JButton("Delete");
    private ActionListener constraintEditListener = e -> {};
    private ActionListener constraintDeleteListener = e -> {};
    // Placement
    private final JLabel placementName = new JLabel(PLACEHOLDER);
    private final JLabel placementGroup = new JLabel(PLACEHOLDER);
    private final JLabel placementDuration = new JLabel(PLACEHOLDER);
    private final JLabel placementStart = new JLabel(PLACEHOLDER);
    private final JLabel placementEnd = new JLabel(PLACEHOLDER);

    public DetailPanel(MainApplicationUI parent, ControllerCaller caller) {
        this.parent = parent;
        this.caller = caller;
        this.cardLayout = new CardLayout();
        initUI();
    }

    public void initUI() {
        setLayout(cardLayout);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setPreferredSize(new Dimension(380, 0));
        add(createWelcomeScreen(), "WELCOME");
        add(createTaskScreen(), "TASK");
        add(createConstraintScreen(), "CONSTRAINT");
        add(createPlacementScreen(), "PLACEMENT");
        setVisible(true);
    }

    public JPanel createWelcomeScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcome = new JLabel("Welcome to Time Planning!");
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 22f));

        panel.add(welcome, BorderLayout.NORTH);

        String introductionText = "[Insert Introduction Here]";
        JLabel introduction = new JLabel(introductionText);

        panel.add(introduction);

        String documentationText = "[Insert Documentation Reference Here]";
        JLabel documentation = new JLabel(documentationText);

        panel.add(documentation, BorderLayout.SOUTH);

        return panel;
    }

    public JPanel createTaskScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        addHeader(panel, "Task");

        JPanel body = new JPanel(new GridLayout(0, 2, 8, 4));
        body.add(new JLabel("Name: "));
        body.add(taskName);
        // TODO Description.
        body.add(new JLabel("Group: "));
        body.add(taskGroup);
        body.add(new JLabel("Duration: "));
        body.add(taskDuration);
        body.add(new JLabel("Earliest Start: "));
        body.add(taskEarliest);
        body.add(new JLabel("Latest End: "));
        body.add(taskLatest);

        panel.add(body, BorderLayout.CENTER);

        addActions(panel, taskEdit, taskDelete);
        return panel;
    }

    public JPanel createConstraintScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        addHeader(panel, "Constraint");

        JPanel body = new JPanel(new GridLayout(0, 2, 8, 4));
        body.add(new JLabel("Source Group: "));
        body.add(constraintSource);
        body.add(new JLabel("Target Group: "));
        body.add(constraintTarget);
        body.add(new JLabel("Min. Gap: "));
        body.add(constraintMin);
        body.add(new JLabel("Max. Gap: "));
        body.add(constraintMax);

        panel.add(body, BorderLayout.CENTER);

        addActions(panel, constraintEdit, constraintDelete);
        return panel;
    }

    public JPanel createPlacementScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        addHeader(panel, "Placement");

        JPanel body = new JPanel(new GridLayout(0, 2, 8, 4));
        body.add(new JLabel("Name: "));
        body.add(placementName);
        // TODO Description.
        body.add(new JLabel("Group: "));
        body.add(placementGroup);
        body.add(new JLabel("Duration: "));
        body.add(placementDuration);
        body.add(new JLabel("Start: "));
        body.add(placementStart);
        body.add(new JLabel("End: "));
        body.add(placementEnd);

        panel.add(body, BorderLayout.CENTER);

        return panel;
    }

    private void addHeader(JPanel panel, String text) {
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        header.add(title, BorderLayout.WEST);
        JButton close = new JButton("×");
        close.setFont(close.getFont().deriveFont(Font.BOLD, 16f));
        close.setMargin(new Insets(0, 8, 0, 8));
        close.addActionListener(e -> {
            cardLayout.show(DetailPanel.this, "WELCOME");
            DetailPanel.this.parent.clearDetailSelection();
        });
        header.add(close, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);
    }

    private void addActions(JPanel panel, JButton edit, JButton delete) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (edit != null) actions.add(edit);
        if (delete != null) actions.add(delete);

        panel.add(actions, BorderLayout.SOUTH);
    }

    public void showWelcome() {
        cardLayout.show(this, "WELCOME");
    }

    public void showTask(Task task) {
        taskName.setText(task.name());
        taskGroup.setText(task.group());
        taskDuration.setText(task.duration().toString());
        taskEarliest.setText(task.timeWindow().earliestStart().toString());
        taskLatest.setText(task.timeWindow().latestEnd().toString());

        if (taskEditListener != null) taskEdit.removeActionListener(taskEditListener);
        if (taskDeleteListener != null) taskDelete.removeActionListener(taskDeleteListener);

        taskEditListener = e -> TaskDialog.show(parent, task, caller::callUpdateTask);
        taskDeleteListener = e -> caller.deleteTask(task);

        taskEdit.addActionListener(taskEditListener);
        taskDelete.addActionListener(taskDeleteListener);

        cardLayout.show(this, "TASK");
    }

    public void showConstraint(GroupConstraint constraint) {
        constraintSource.setText(constraint.sourceGroup());
        constraintTarget.setText(constraint.targetGroup());
        constraintMin.setText(constraint.minimumGap() != null ? constraint.minimumGap().toString() : "N/A");
        constraintMax.setText(constraint.maximumGap() != null ? constraint.maximumGap().toString() : "N/A");

        if (constraintEditListener != null) constraintEdit.removeActionListener(constraintEditListener);
        if (constraintDeleteListener != null) constraintDelete.removeActionListener(constraintDeleteListener);

        constraintEditListener = e -> ConstraintDialog.show(parent, constraint, caller::callUpdateConstraint);
        constraintDeleteListener = e -> caller.deleteConstraint(constraint);

        constraintEdit.addActionListener(constraintEditListener);
        constraintDelete.addActionListener(constraintDeleteListener);

        cardLayout.show(this, "CONSTRAINT");
    }

    public void showPlacement(TaskPlacement placement) {
        Task task = placement.task();
        placementName.setText(task.name());
        placementGroup.setText(task.group());
        placementDuration.setText(task.duration().toString());
        placementStart.setText(placement.start().toString());
        placementEnd.setText(placement.end().toString());

        cardLayout.show(this, "PLACEMENT");
    }

}
