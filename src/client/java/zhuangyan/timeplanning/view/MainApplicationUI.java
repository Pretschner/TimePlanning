package zhuangyan.timeplanning.view;

import zhuangyan.timeplanning.controller.*;
import zhuangyan.timeplanning.model.*;
import zhuangyan.timeplanning.time.TimeConverter;
import zhuangyan.timeplanning.view.dialogs.*;
import zhuangyan.timeplanning.view.renderers.*;
import zhuangyan.timeplanning.view.renderers.TimetableCell.SlotType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The main dashboard: three tabs (Tasks, Constraints, Timetable) with add/edit/delete, template generation, and schedule navigation. */
public class MainApplicationUI extends JFrame {
    // Controller Access
    private final ControllerCaller controllerCaller;

    // Tabbed Pane
    private JTabbedPane tabbedPane;

    // Tasks
    private JList<Task> taskList;
    private DefaultListModel<Task> taskListModel;

    // GroupConstraints
    private JList<GroupConstraint> constraintList;
    private DefaultListModel<GroupConstraint> constraintListModel;

    // Timetable
    private JTable timetableTable;
    private DefaultTableModel timetableModel;
    private TimeConverter timeConverter;

    // Add and Template buttons
    private JButton addButton, templateButton;

    // Timetable navigation
    private List<Schedule> schedulesList = new ArrayList<>();
    private int currentScheduleIndex = 0;
    private JLabel scheduleNavLabel;
    private JButton prevScheduleButton;
    private JButton nextScheduleButton;

    // Detail Panel
    private DetailPanel detailPanel;

    // Default Config for Schedule Generation / Rendering
    private int slotInMinutes = 15;
    private LocalTime lowerBound = LocalTime.of(7, 0);
    private LocalTime upperBound = LocalTime.of(23, 0);


    public MainApplicationUI() {
        this.controllerCaller = new ControllerCaller(this);
        initUI();
        controllerCaller.syncAllDataFromServer();
    }

    private void initUI() {
        setTitle("Time Planning – Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1480, 700);
        setLocationRelativeTo(null);

        // Top Panel (Name, Help, Logout)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel logo = new JLabel("Time Planning");
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 18f));
        topPanel.add(logo, BorderLayout.WEST);

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton settingsBtn = new JButton("Settings");
        settingsBtn.addActionListener(e -> openSettings());
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        topRightPanel.add(settingsBtn);
        topRightPanel.add(logoutBtn);
        topPanel.add(topRightPanel, BorderLayout.EAST);

        // Detail Panel (East)
        detailPanel = new DetailPanel(this, controllerCaller);

        // Center (Tabs)
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tasks", createTaskPanel());
        tabbedPane.addTab("Constraints", createConstraintPanel());
        tabbedPane.addTab("Timetable", createTimetablePanel());

        // Bottom Panel (Generate, Add, Edit, Delete)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        // Bottom buttons
        JButton generateBtn = new JButton("⚡ Generate Schedule");
        generateBtn.setPreferredSize(new Dimension(160, 32));
        generateBtn.addActionListener(e -> {
            ScheduleDialog.show(this, slotInMinutes, controllerCaller::generateSchedule);
        });

        addButton = new JButton("Add");
        addButton.addActionListener(e -> handleAdd());

        templateButton = new JButton("Templates");
        templateButton.addActionListener(e -> handleTemplate());

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 28));

        bottomPanel.add(generateBtn);
        bottomPanel.add(addButton);
        bottomPanel.add(templateButton);

        // Assemble
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(detailPanel, BorderLayout.EAST);

        tabbedPane.addChangeListener(e -> {
            detailPanel.showWelcome();
            updateButtonStateForCurrentTab();
        });
        detailPanel.showWelcome();
    }

    // TAB PANELS

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setCellRenderer(new TaskCardRenderer());
        taskList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        taskList.setVisibleRowCount(-1);
        taskList.setBackground(Color.WHITE);

        taskList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Task selectedTask = taskList.getSelectedValue();
                if (selectedTask != null) detailPanel.showTask(selectedTask);
            }
        });

        panel.add(new JScrollPane(taskList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createConstraintPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        constraintListModel = new DefaultListModel<>();
        constraintList = new JList<>(constraintListModel);
        constraintList.setCellRenderer(new ConstraintCardRenderer());
        constraintList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        constraintList.setVisibleRowCount(-1);
        constraintList.setBackground(Color.WHITE);

        constraintList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                GroupConstraint selectedConstraint = constraintList.getSelectedValue();
                if (selectedConstraint != null) detailPanel.showConstraint(selectedConstraint);
            }
        });

        panel.add(new JScrollPane(constraintList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table with Dummy Model
        timetableTable = new JTable(new DefaultTableModel(0, 0));
        timetableTable.setRowHeight(25);
        timetableTable.setDefaultRenderer(Object.class, new TimetableRenderer());

        // Single Cell Selection
        timetableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timetableTable.setCellSelectionEnabled(true);

        Runnable selectionUpdater = () -> {
            SwingUtilities.invokeLater(() -> {
                int row = timetableTable.getSelectedRow();
                int col = timetableTable.getSelectedColumn();
                if (row >= 0 && col > 0) {
                    Object value = timetableModel.getValueAt(row, col);
                    if (value instanceof TimetableCell slot) {
                        detailPanel.showPlacement(slot.scheduledTask());
                    }
                }
            });
        };

        timetableTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectionUpdater.run();
        });

        timetableTable.getColumnModel().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectionUpdater.run();
        });


        JScrollPane scrollPane = new JScrollPane(timetableTable);

        // Navigation label
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        scheduleNavLabel = new JLabel("Schedule 1/1");
        navPanel.add(scheduleNavLabel);

        // Arrow buttons
        prevScheduleButton = new JButton("<");
        prevScheduleButton.setFont(prevScheduleButton.getFont().deriveFont(18f));
        prevScheduleButton.setPreferredSize(new Dimension(50, 60));
        prevScheduleButton.addActionListener(e -> navigateSchedule(-1));

        nextScheduleButton = new JButton(">");
        nextScheduleButton.setFont(nextScheduleButton.getFont().deriveFont(18f));
        nextScheduleButton.setPreferredSize(new Dimension(50, 60));
        nextScheduleButton.addActionListener(e -> navigateSchedule(1));

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(prevScheduleButton, BorderLayout.WEST);
        centerWrapper.add(scrollPane, BorderLayout.CENTER);
        centerWrapper.add(nextScheduleButton, BorderLayout.EAST);

        // Assembling
        panel.add(navPanel, BorderLayout.NORTH);
        panel.add(centerWrapper, BorderLayout.CENTER);

        rebuildTimetableModel();
        updateNavigationButtons();
        return panel;
    }

    private void rebuildTimetableModel() {
        // Create TimeConverter with view window if not already created
        if (timeConverter == null) {
            timeConverter = TimeConverter.create(slotInMinutes, lowerBound, upperBound);
        }

        // Generate Rows (Time Slots) and Columns ("Start Time" + Days)
        String[] timeSlots = timeConverter.getRowLabels();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] columns = new String[days.length + 1];
        columns[0] = "Start Time";
        System.arraycopy(days, 0, columns, 1, days.length);

        // Override Dummy Table Model
        timetableModel = new DefaultTableModel(columns, timeSlots.length) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        // Fill 1st Column
        for (int i = 0; i < timeSlots.length; i++) {
            timetableModel.setValueAt(timeSlots[i], i, 0);
        }

        timetableTable.setModel(timetableModel);

        // Apply Column Headers
        for (int i = 0; i < days.length; i++) {
            timetableTable.getColumnModel().getColumn(i + 1).setHeaderValue(days[i]);
        }

        // Redraw Schedule (if displayed)
        if (!schedulesList.isEmpty() && currentScheduleIndex < schedulesList.size()) {
            displaySchedule(currentScheduleIndex);
        } else {
            clearTimetable();
        }

        timetableTable.repaint();
    }

    //  BOTTOM BUTTON LISTENERS

    private void handleAdd() {
        int idx = tabbedPane.getSelectedIndex();
        if (idx == 0) TaskDialog.show(this, null, controllerCaller::callAddTask);
        else ConstraintDialog.show(this, null, controllerCaller::callAddConstraint); // idx == 1
    }

    private void handleTemplate() {
        int idx = tabbedPane.getSelectedIndex();
        if (idx == 0) TaskTemplateDialog.show(this, controllerCaller::callAddTask);
        else ConstraintTemplateDialog.show(this, controllerCaller::callAddConstraint); // idx == 1
    }

    // UI UPDATES

    public void updateTaskTable(List<Task> tasks) {
        SwingUtilities.invokeLater(() -> {
            taskListModel.clear();
            for (Task t : tasks) {
                taskListModel.addElement(t);
            }
            taskList.clearSelection();
        });
    }

    public void updateConstraintTable(List<GroupConstraint> constraints) {
        SwingUtilities.invokeLater(() -> {
            constraintListModel.clear();
            for (GroupConstraint c : constraints) {
                constraintListModel.addElement(c);
            }
            constraintList.clearSelection();
        });
    }

    public void updateTimetable(List<Schedule> schedules) {
        SwingUtilities.invokeLater(() -> {
            this.schedulesList = new ArrayList<>(schedules);
            if (schedulesList.isEmpty()) {
                clearTimetable();
                currentScheduleIndex = 0;
                updateNavigationButtons();
                return;
            }
            currentScheduleIndex = 0;
            displaySchedule(0);
        });
    }

    private void clearTimetable() {
        if (timetableModel == null) return;
        int rowCount = timetableModel.getRowCount();
        int colCount = timetableModel.getColumnCount();
        for (int row = 0; row < rowCount; row++) {
            for (int col = 1; col < colCount; col++) { // Skip Time Labels
                timetableModel.setValueAt(null, row, col);
            }
        }
        timetableTable.repaint();
    }

    /**
     * Method that fills the DefaultTableModel with TimetableCell objects for rendering.
     * @param index the index in schedulesList at which the desired Schedule is located.
     * */
    private void displaySchedule(int index) {
        clearTimetable();

        // Validation
        if (schedulesList == null || schedulesList.isEmpty() || index < 0 || index >= schedulesList.size()) {
            updateNavigationButtons();
            return;
        }
        Schedule schedule = schedulesList.get(index);
        if (schedule == null || schedule.scheduledTasks() == null || schedule.scheduledTasks().isEmpty()) {
            updateNavigationButtons();
            return;
        }

        for (ScheduledTask scheduledTask : schedule.scheduledTasks()) {
            int visibleCount = 0;
            Task task = scheduledTask.task();
            int startSlot = timeConverter.fromTimePoint(scheduledTask.start());
            int durationSlots = timeConverter.fromDuration(task.duration());
            int slotsPerDay = timeConverter.getSlotsPerDay();

            for (int offset = 0; offset < durationSlots; offset++) {
                visibleCount++;
                int slot = (startSlot + offset) % timeConverter.getSlotsPerWeek();
                int dayIndex = slot / slotsPerDay;
                int row = timeConverter.toRow(slot);

                if (row < 0) continue; // Outside visible window

                int col = dayIndex + 1; // col 0 = time labels

                SlotType type = switch (visibleCount) {
                    case 1 -> SlotType.NAME_DISPLAY;
                    case 2 -> SlotType.TIME_DISPLAY;
                    default -> SlotType.BACKGROUND;
                };

                timetableModel.setValueAt(new TimetableCell(scheduledTask, type), row, col);
            }
        }

        timetableTable.repaint();
        updateNavigationButtons();
    }

    // BUTTON STATE

    private void updateButtonStateForCurrentTab() {
        int idx = tabbedPane.getSelectedIndex();
        boolean isTaskOrConstraintTab = idx <= 1;
        addButton.setVisible(isTaskOrConstraintTab);
        templateButton.setVisible(isTaskOrConstraintTab);
    }

    // TOP PANEL BUTTON LISTENERS

    private void openSettings() {
        SettingsUI settingsUI = new SettingsUI(this, slotInMinutes, lowerBound, upperBound);
        // If OK
        if (settingsUI.isConfirmed()) {
            // Pull Changes to Settings
            this.slotInMinutes = settingsUI.getSlotInMinutes();
            this.lowerBound = settingsUI.getLowerBound();
            this.upperBound = settingsUI.getUpperBound();

            // Recreate TimeConverter with new settings
            this.timeConverter = TimeConverter.create(slotInMinutes, lowerBound, upperBound);

            // Update Table Layout and Content
            rebuildTimetableModel();
        }
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> new AuthenticationUI().setVisible(true));
    }

    // Timetable Navigation
    private void navigateSchedule(int delta) {
        int newIndex = currentScheduleIndex + delta;
        if (newIndex < 0 || newIndex >= schedulesList.size()) return;
        currentScheduleIndex = newIndex;
        displaySchedule(currentScheduleIndex);
    }

    private void updateNavigationButtons() {
        if (schedulesList == null || schedulesList.isEmpty()) {
            prevScheduleButton.setEnabled(false);
            nextScheduleButton.setEnabled(false);
            scheduleNavLabel.setText("No schedules");
            return;
        }
        prevScheduleButton.setEnabled(currentScheduleIndex > 0);
        nextScheduleButton.setEnabled(currentScheduleIndex < schedulesList.size() - 1);
        scheduleNavLabel.setText("Schedule " + (currentScheduleIndex + 1) + "/" + schedulesList.size());
    }

    public void clearDetailSelection() {
        taskList.clearSelection();
        constraintList.clearSelection();
        timetableTable.clearSelection();
        detailPanel.showWelcome();
    }
}