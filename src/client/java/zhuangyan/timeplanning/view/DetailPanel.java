package zhuangyan.timeplanning.view;

import zhuangyan.timeplanning.controller.ControllerCaller;
import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.model.ScheduledTask;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.view.dialogs.ConstraintDialog;
import zhuangyan.timeplanning.view.dialogs.TaskDialog;
import zhuangyan.timeplanning.view.util.ColorSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DetailPanel extends JPanel {
    private static final float H1 = 28f;
    private static final float H2 = 24f;
    private static final float H3 = 18f;
    private final MainApplicationUI parent;
    private final ControllerCaller caller;
    private final CardLayout cardLayout;
    private static final String PLACEHOLDER = "--Placeholder--";

    // Task
    private final JLabel taskName = new JLabel(PLACEHOLDER);
    private final JLabel taskGroup = new JLabel(PLACEHOLDER);
    private final JLabel taskDuration = new JLabel(PLACEHOLDER);
    private final JLabel taskEarliest = new JLabel(PLACEHOLDER);
    private final JLabel taskLatest = new JLabel(PLACEHOLDER);
    private final JButton taskEdit = new JButton("Edit");
    private final JButton taskDelete = new JButton("Delete");
    private ActionListener taskEditListener = e -> {};
    private ActionListener taskDeleteListener = e -> {};
    private final JPanel taskAccentNorth = new JPanel();
    private final JPanel taskAccentSouth = new JPanel();
    private final JLabel taskColorDot = new JLabel();
    // Constraint
    private final JLabel constraintSource = new JLabel(PLACEHOLDER);
    private final JLabel constraintTarget = new JLabel(PLACEHOLDER);
    private final JLabel constraintMin = new JLabel(PLACEHOLDER);
    private final JLabel constraintMax = new JLabel(PLACEHOLDER);
    private final JButton constraintEdit = new JButton("Edit");
    private final JButton constraintDelete = new JButton("Delete");
    private ActionListener constraintEditListener = e -> {};
    private ActionListener constraintDeleteListener = e -> {};
    private final JPanel constraintAccentNorth = new JPanel();
    private final JPanel constraintAccentSouth = new JPanel();
    private final JLabel constraintSourceColorDot = new JLabel();
    private final JLabel constraintTargetColorDot = new JLabel();
    // Placement
    private final JLabel placementName = new JLabel(PLACEHOLDER);
    private final JLabel placementGroup = new JLabel(PLACEHOLDER);
    private final JLabel placementDuration = new JLabel(PLACEHOLDER);
    private final JLabel placementStart = new JLabel(PLACEHOLDER);
    private final JLabel placementEnd = new JLabel(PLACEHOLDER);
    private final JPanel placementAccentNorth = new JPanel();
    private final JPanel placementAccentSouth = new JPanel();
    private final JLabel placementColorDot = new JLabel();

    public DetailPanel(MainApplicationUI parent, ControllerCaller caller) {
        this.parent = parent;
        this.caller = caller;
        this.cardLayout = new CardLayout();
        initUI();
    }

    public void initUI() {
        setLayout(cardLayout);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setPreferredSize(new Dimension(380, 0));
        add(createWelcomeScreen(), "WELCOME");
        add(createTaskScreen(), "TASK");
        add(createConstraintScreen(), "CONSTRAINT");
        add(createPlacementScreen(), "PLACEMENT");
        setVisible(true);
    }

    public JPanel createWelcomeScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel welcome = new JLabel("<html>" + "Welcome to Time Planning!" + "</html>");
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, H1));

        panel.add(welcome, BorderLayout.NORTH);

        String introductionText =
                "<html> This application is dedicated to helping you keep track of your tasks. <br>" +
                "Not by having a simple list for them, but by arranging them in a timetable that you can follow. <br>" +
                "Hope you like it! </html>";
        JLabel introduction = new JLabel(introductionText);
        introduction.setFont(introduction.getFont().deriveFont(Font.PLAIN, 14f));
        panel.add(introduction, BorderLayout.CENTER);

        String documentationText = "<html> <b> How to get started: </b> <br>" +
                "1. Create tasks using the 'Add' button, or generate from 'Templates' while you're in the 'Tasks' tab. <br>" +
                "2. Create constraints in a similar fashion while you're in the 'Constraints' tab. <br>" +
                "3. Make sure all durations align to the slot length specified in the settings. <br>" +
                "4. Select a Configuration by hitting the 'Generate' button and wait for your schedule to be created. <br>" +
                "5. Head over to the Timetable tab and inspect your schedule! </html>";
        JLabel documentation = new JLabel(documentationText);
        documentation.setFont(documentation.getFont().deriveFont(Font.PLAIN, 14f));

        panel.add(documentation, BorderLayout.SOUTH);

        return panel;
    }

    public JPanel createTaskScreen() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top Line (Accent + Headline and Close)
        addHeader(panel, "Task", taskAccentNorth);

        JPanel body = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Row 0: Task Name
        gbc.gridy = 0;
        gbc.insets = new Insets(16, 0, 8, 0);
        taskName.setFont(taskName.getFont().deriveFont(Font.BOLD, H2));
        body.add(taskName, gbc);

        // Row 1: Colored Dot + Group Text
        gbc.gridy = 1;
        JPanel groupChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        taskColorDot.setPreferredSize(new Dimension(16, 16));
        taskColorDot.setOpaque(true);
        taskGroup.setFont(taskGroup.getFont().deriveFont(Font.PLAIN, H3));
        groupChip.add(taskColorDot);
        groupChip.add(taskGroup);
        body.add(groupChip, gbc);

        // Row 2: Separator
        gbc.gridy = 2;
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(0xDBDBDB));
        body.add(separator, gbc);

        // Row 3: Metadata Grid
        gbc.gridy = 3;
        JPanel metadataPanel = new JPanel(new GridLayout(3, 2, 24, 16));
        addMetadataRow(metadataPanel, "Duration:", taskDuration);
        addMetadataRow(metadataPanel, "Earliest Start:", taskEarliest);
        addMetadataRow(metadataPanel, "Latest End:", taskLatest);
        body.add(metadataPanel, gbc);

        panel.add(body, BorderLayout.CENTER);

        // Bottom Line (Edit/Delete + Accent)
        addFooter(panel, taskEdit, taskDelete, taskAccentSouth);

        return panel;
    }

    public JPanel createConstraintScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        addHeader(panel, "Constraint", constraintAccentNorth);

        JPanel body = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Row 0: Constraint Source
        gbc.gridy = 0;
        gbc.insets = new Insets(16, 0, 8, 0);
        JLabel source = new JLabel("Source:");
        source.setFont(source.getFont().deriveFont(Font.PLAIN, H2));
        body.add(source, gbc);
        gbc.gridx = 1;
        JPanel sourceGroupChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        constraintSourceColorDot.setPreferredSize(new Dimension(16, 16));
        constraintSourceColorDot.setOpaque(true);
        constraintSource.setFont(constraintSource.getFont().deriveFont(Font.BOLD, H2));
        sourceGroupChip.add(constraintSourceColorDot);
        sourceGroupChip.add(constraintSource);
        body.add(sourceGroupChip, gbc);

        // Row 1: Constraint Target
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel target = new JLabel("Target:");
        target.setFont(target.getFont().deriveFont(Font.PLAIN, H2));
        body.add(target, gbc);
        gbc.gridx = 1;
        JPanel targetGroupChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        constraintTargetColorDot.setPreferredSize(new Dimension(16, 16));
        constraintTargetColorDot.setOpaque(true);
        constraintTarget.setFont(constraintTarget.getFont().deriveFont(Font.BOLD, H2));
        targetGroupChip.add(constraintTargetColorDot);
        targetGroupChip.add(constraintTarget);
        body.add(targetGroupChip, gbc);

        // Row 2: Separator
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(0xDBDBDB));
        body.add(separator, gbc);

        // Row 3: Metadata Grid
        gbc.gridy = 3;
        JPanel metadataPanel = new JPanel(new GridLayout(3, 2, 24, 16));
        addMetadataRow(metadataPanel, "Min. Gap:", constraintMin);
        addMetadataRow(metadataPanel, "Max. Gap:", constraintMax);
        body.add(metadataPanel, gbc);

        panel.add(body, BorderLayout.CENTER);

        // Bottom Line (Edit/Delete + Accent)
        addFooter(panel, constraintEdit, constraintDelete, constraintAccentSouth);
        return panel;
    }

    public JPanel createPlacementScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        addHeader(panel, "Placement", placementAccentNorth);

        JPanel body = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Row 0: Placement Name
        gbc.gridy = 0;
        gbc.insets = new Insets(16, 0, 8, 0);
        placementName.setFont(placementName.getFont().deriveFont(Font.BOLD, H2));
        body.add(placementName, gbc);

        // Row 1: Colored Dot + Group Text
        gbc.gridy = 1;
        JPanel groupChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        placementColorDot.setPreferredSize(new Dimension(16, 16));
        placementColorDot.setOpaque(true);
        placementGroup.setFont(placementGroup.getFont().deriveFont(Font.PLAIN, H3));
        groupChip.add(placementColorDot);
        groupChip.add(placementGroup);
        body.add(groupChip, gbc);

        // Row 2: Separator
        gbc.gridy = 2;
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(0xDBDBDB));
        body.add(separator, gbc);

        // TODO Description.

        // Row 3: Metadata Grid
        gbc.gridy = 3;
        JPanel metadataPanel = new JPanel(new GridLayout(3, 2, 24, 16));
        addMetadataRow(metadataPanel, "Duration:", placementDuration);
        addMetadataRow(metadataPanel, "Start:", placementStart);
        addMetadataRow(metadataPanel, "End:", placementEnd);
        body.add(metadataPanel, gbc);

        addFooter(panel, null, null, placementAccentSouth);

        panel.add(body, BorderLayout.CENTER);

        return panel;
    }

    private void addHeader(JPanel panel, String text, JPanel accentNorth) {
        JPanel header = new JPanel(new BorderLayout());

        accentNorth.setPreferredSize(new Dimension(0, 12));
        header.add(accentNorth, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        JLabel title = new JLabel(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, H1));
        content.add(title, BorderLayout.WEST);
        JButton close = new JButton("×");
        close.setFont(close.getFont().deriveFont(Font.BOLD, 16f));
        close.addActionListener(e -> {
            cardLayout.show(DetailPanel.this, "WELCOME");
            DetailPanel.this.parent.clearDetailSelection();
        });
        content.add(close, BorderLayout.EAST);

        header.add(content, BorderLayout.CENTER);

        panel.add(header, BorderLayout.NORTH);
    }

    private void addFooter(JPanel panel, JButton edit, JButton delete, JPanel accentSouth) {
        JPanel footer = new JPanel(new BorderLayout());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (edit != null) actions.add(edit);
        if (delete != null) actions.add(delete);

        accentSouth.setPreferredSize(new Dimension(0, 12));

        footer.add(actions, BorderLayout.CENTER);
        footer.add(accentSouth, BorderLayout.SOUTH);

        panel.add(footer, BorderLayout.SOUTH);
    }

    private void addMetadataRow(JPanel grid, String label, JLabel value) {
        JLabel keyLabel = new JLabel(label);
        keyLabel.setForeground(new Color(0x7F8C8D));
        keyLabel.setFont(keyLabel.getFont().deriveFont(Font.PLAIN, 16f));
        keyLabel.setHorizontalAlignment(SwingConstants.LEFT);
        value.setFont(value.getFont().deriveFont(Font.PLAIN, 16f));
        value.setForeground(new Color(0x2C3E50));
        grid.add(keyLabel);
        grid.add(value);
    }

    public void showWelcome() {
        cardLayout.show(this, "WELCOME");
    }

    public void showTask(Task task) {
        // Colours
        taskAccentNorth.setBackground(ColorSelector.colorForGroup(task.group()));
        taskAccentSouth.setBackground(ColorSelector.colorForGroup(task.group()));
        taskColorDot.setBackground(ColorSelector.colorForGroup(task.group()));

        // Fields
        taskName.setText("<html>" + task.name() + "</html>");
        taskGroup.setText(task.group());
        taskDuration.setText(DurationUtil.formatDurationString(task.duration()));
        taskEarliest.setText(task.timeWindow().earliestStart().toPrettyString());
        taskLatest.setText(task.timeWindow().latestEnd().toPrettyString());

        // Listeners
        if (taskEditListener != null) taskEdit.removeActionListener(taskEditListener);
        if (taskDeleteListener != null) taskDelete.removeActionListener(taskDeleteListener);
        taskEditListener = e -> TaskDialog.show(parent, task, caller::callUpdateTask);
        taskDeleteListener = e -> {
            caller.deleteTask(task);
            parent.clearDetailSelection();
        };
        taskEdit.addActionListener(taskEditListener);
        taskDelete.addActionListener(taskDeleteListener);

        cardLayout.show(this, "TASK");
    }

    public void showConstraint(GroupConstraint constraint) {
        // Colors
        constraintAccentNorth.setBackground(ColorSelector.colorForGroup(constraint.sourceGroup()));
        constraintAccentSouth.setBackground(ColorSelector.colorForGroup(constraint.targetGroup()));
        constraintSourceColorDot.setBackground(ColorSelector.colorForGroup(constraint.sourceGroup()));
        constraintTargetColorDot.setBackground(ColorSelector.colorForGroup(constraint.targetGroup()));

        // Fields
        constraintSource.setText(constraint.sourceGroup());
        constraintTarget.setText(constraint.targetGroup());
        constraintMin.setText(constraint.minimumGap() != null ? (constraint.minimumGap().toMinutes() + " min") : "N/A");
        constraintMax.setText(constraint.maximumGap() != null ? (constraint.maximumGap().toMinutes() + " min") : "N/A");

        // Listeners
        if (constraintEditListener != null) constraintEdit.removeActionListener(constraintEditListener);
        if (constraintDeleteListener != null) constraintDelete.removeActionListener(constraintDeleteListener);
        constraintEditListener = e -> ConstraintDialog.show(parent, constraint, caller::callUpdateConstraint);
        constraintDeleteListener = e -> {
            caller.deleteConstraint(constraint);
            parent.clearDetailSelection();
        };
        constraintEdit.addActionListener(constraintEditListener);
        constraintDelete.addActionListener(constraintDeleteListener);

        cardLayout.show(this, "CONSTRAINT");
    }

    public void showPlacement(ScheduledTask scheduledTask) {
        Task task = scheduledTask.task();

        // Colors
        placementAccentNorth.setBackground(ColorSelector.colorForGroup(task.group()));
        placementAccentSouth.setBackground(ColorSelector.colorForGroup(task.group()));
        placementColorDot.setBackground(ColorSelector.colorForGroup(task.group()));

        // Fields
        placementName.setText("<html>" + task.name() + "</html>");
        placementGroup.setText(task.group());
        placementDuration.setText(DurationUtil.formatDurationString(task.duration()));
        placementStart.setText(scheduledTask.start().toPrettyString());
        placementEnd.setText(scheduledTask.end().toPrettyString());

        cardLayout.show(this, "PLACEMENT");
    }

}
