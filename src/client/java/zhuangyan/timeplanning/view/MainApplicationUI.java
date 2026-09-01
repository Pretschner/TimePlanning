package zhuangyan.timeplanning.view;

import zhuangyan.timeplanning.controller.*;
import zhuangyan.timeplanning.model.*;
import zhuangyan.timeplanning.time.TimetableGrid;
import zhuangyan.timeplanning.view.dialogs.*;
import zhuangyan.timeplanning.view.renderers.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MainApplicationUI extends JFrame {
    /**
     * Main UI handling CRUD operations for Tasks and Constraints and Schedule Generation
     */
    // Controller Access
    private final ControllerCaller controllerCaller = new ControllerCaller(this);

    // UI Components
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
    private TimetableGrid timetableGrid;

    // Timetable navigation
    private List<Schedule> schedulesList = new ArrayList<>();
    private int currentScheduleIndex = 0;
    private JLabel scheduleNavLabel;
    private JButton prevScheduleButton;
    private JButton nextScheduleButton;

    // Bottom buttons
    private JButton generateBtn;
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton templateButton;

    // Selected Tasks/Constraints
    private Task selectedTask = null;
    private GroupConstraint selectedConstraint = null;

    // Default Config for Schedule Generation / Rendering
    private int slotInMinutes = 15;
    private LocalTime lowerBound = LocalTime.of(7, 0);
    private LocalTime upperBound = LocalTime.of(23, 0);


    public MainApplicationUI() {
        initUI();
        controllerCaller.syncAllDataFromServer();
    }

    private void initUI() {
        setTitle("Time Planning – Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // Top Panel (Name, Help, Logout)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel logo = new JLabel("TimePlanning");
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

        // Center (Tabs)
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tasks", createTaskPanel());
        tabbedPane.addTab("Constraints", createConstraintPanel());
        tabbedPane.addTab("Timetable", createTimetablePanel());

        // Bottom Panel (Generate, Add, Edit, Delete)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        generateBtn = new JButton("⚡ Generate Schedule");
        generateBtn.setPreferredSize(new Dimension(160, 32));
        generateBtn.addActionListener(e -> {
            ScheduleDialog.show(this, slotInMinutes, controllerCaller::generateSchedule);
        });

        addBtn = new JButton("Add");
        addBtn.addActionListener(e -> handleAdd());

        editBtn = new JButton("Edit");
        editBtn.setEnabled(false);
        editBtn.addActionListener(e -> handleEdit());

        deleteBtn = new JButton("Delete");
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(e -> handleDelete());

        templateButton = new JButton("Templates");
        templateButton.addActionListener(e -> handleTemplate());

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 28));

        bottomPanel.add(generateBtn);
        bottomPanel.add(sep);
        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(templateButton);

        // Assemble
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        tabbedPane.addChangeListener(e -> updateButtonStateForCurrentTab());
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
                selectedTask = taskList.getSelectedValue();
                updateButtonStateForCurrentTab();
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
                selectedConstraint = constraintList.getSelectedValue();
                updateButtonStateForCurrentTab();
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
        timetableGrid = new TimetableGrid(lowerBound, upperBound, slotInMinutes);

        // Generate Rows (Time Slots) and Columns ("Start Time" + Days)
        String[] timeSlots = timetableGrid.rowLabels();
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


    // BUTTON STATE

    private void updateButtonStateForCurrentTab() {
        int idx = tabbedPane.getSelectedIndex();
        if (idx == 0) { // Tasks
            boolean hasSelection = selectedTask != null;
            editBtn.setEnabled(hasSelection);
            deleteBtn.setEnabled(hasSelection);
            addBtn.setEnabled(true);
        } else if (idx == 1) { // Constraints
            boolean hasSelection = selectedConstraint != null;
            editBtn.setEnabled(hasSelection);
            deleteBtn.setEnabled(hasSelection);
            addBtn.setEnabled(true);
        } else { // Timetable
            editBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
            addBtn.setEnabled(false);
        }
    }

    //  BOTTOM BUTTON LISTENERS

    private void handleAdd() {
        int idx = tabbedPane.getSelectedIndex();
        if (idx == 0) TaskDialog.show(this, null, controllerCaller::callAddTask);
        else ConstraintDialog.show(this, null, controllerCaller::callAddConstraint); // idx == 1
    }

    private void handleEdit() {
        int idx = tabbedPane.getSelectedIndex();
        if (idx == 0 && selectedTask != null) TaskDialog.show(this, selectedTask, controllerCaller::callUpdateTask);
        else if (idx == 1 && selectedConstraint != null) ConstraintDialog.show(this, selectedConstraint, controllerCaller::callUpdateConstraint);
        else JOptionPane.showMessageDialog(this, "Please select an item to edit.");
    }

    private void handleDelete() {
        int idx = tabbedPane.getSelectedIndex();
        if (idx == 0 && selectedTask != null) controllerCaller.deleteTask(selectedTask);
        else if (idx == 1 && selectedConstraint != null) controllerCaller.deleteConstraint(selectedConstraint);
        else JOptionPane.showMessageDialog(this, "Please select an item to delete.");
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
            selectedTask = null;
            updateButtonStateForCurrentTab();
        });
    }

    public void updateConstraintTable(List<GroupConstraint> constraints) {
        SwingUtilities.invokeLater(() -> {
            constraintListModel.clear();
            for (GroupConstraint c : constraints) {
                constraintListModel.addElement(c);
            }
            constraintList.clearSelection();
            selectedConstraint = null;
            updateButtonStateForCurrentTab();
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

    private static final long MINUTES_PER_DAY = 1440L;
    private static final long MINUTES_PER_WEEK = 7 * MINUTES_PER_DAY;

    private void displaySchedule(int index) {
        clearTimetable();

        // Validation
        if (schedulesList == null || schedulesList.isEmpty() || index < 0 || index >= schedulesList.size()) {
            updateNavigationButtons();
            return;
        }
        Schedule schedule = schedulesList.get(index);
        if (schedule == null || schedule.taskPlacements() == null) {
            updateNavigationButtons();
            return;
        }

        // Table Boundaries (e.g., 8:00 AM and 10:00 PM)
        long lbMinutes = lowerBound.toSecondOfDay() / 60;
        long ubMinutes = upperBound.toSecondOfDay() / 60;

        for (TaskPlacement placement : schedule.taskPlacements()) {
            Task task = placement.task();
            TimePoint startPoint = placement.start();
            long taskDurationMinutes = task.duration().toMinutes();

            // Convert absolute start to minutes since Monday 00:00
            long dayOffsetMinutes = (startPoint.day().getValue() - 1) * MINUTES_PER_DAY;
            long timeOffsetMinutes = startPoint.time().toSecondOfDay() / 60;
            long absoluteStartWeekMinutes = dayOffsetMinutes + timeOffsetMinutes;

            // Normalize to handle tasks that wrap across Sunday midnight
            long taskStartWeekMinutes = absoluteStartWeekMinutes % MINUTES_PER_WEEK;
            if (taskStartWeekMinutes < 0) taskStartWeekMinutes += MINUTES_PER_WEEK;
            long taskEndWeekMinutes = taskStartWeekMinutes + taskDurationMinutes;

            // Unwrapped times used purely for the display text (e.g., "09:00 - 12:00")
            LocalTime displayStartTime = placement.start().time();
            LocalTime displayEndTime = displayStartTime.plus(task.duration());

            // Tracks visible rows to assign NAME (0) and TIME (1)
            int visibleRowCounter = 0;

            // Iterate every day that this task touches
            long currentDayStartMinutes = (taskStartWeekMinutes / MINUTES_PER_DAY) * MINUTES_PER_DAY;
            while (currentDayStartMinutes < taskEndWeekMinutes) {
                long nextDayStartMinutes = currentDayStartMinutes + MINUTES_PER_DAY;

                // Clip task segment to the current 24-hour day
                long taskStartThisDay = Math.max(taskStartWeekMinutes, currentDayStartMinutes);
                long taskEndThisDay = Math.min(taskEndWeekMinutes, nextDayStartMinutes);

                // Clip the visible grid window to the current day
                long gridStartThisDay = currentDayStartMinutes + lbMinutes;
                long gridEndThisDay = currentDayStartMinutes + ubMinutes;

                // Intersect the task segment with the visible grid window
                long visibleStartThisDay = Math.max(taskStartThisDay, gridStartThisDay);
                long visibleEndThisDay = Math.min(taskEndThisDay, gridEndThisDay);

                // If there is any visible overlap on this day
                if (visibleStartThisDay < visibleEndThisDay) {
                    int dayIndex = (int) ((currentDayStartMinutes / MINUTES_PER_DAY) % 7); // 0=Mon ... 6=Sun

                    // Convert clipped minutes back to LocalTime for row lookups
                    LocalTime rowStartTime = minutesToLocalTime((int) (visibleStartThisDay % MINUTES_PER_DAY));
                    LocalTime rowEndTime = minutesToLocalTime((int) (visibleEndThisDay % MINUTES_PER_DAY));

                    // Subtract 1 minute so that an end time of 12:00 doesn't color the 12:00 row
                    int startRow = timetableGrid.rowIndexOf(rowStartTime);
                    int endRow = timetableGrid.rowIndexOf(rowEndTime.minusMinutes(1));

                    if (startRow >= 0 && endRow >= 0) {
                        for (int row = startRow; row <= endRow; row++) {
                            SlotType type = switch (visibleRowCounter) {
                                case 0 -> SlotType.NAME_DISPLAY;
                                case 1 -> SlotType.TIME_DISPLAY;
                                default -> SlotType.BACKGROUND;
                            };

                            // col = dayIndex + 1 (0 = Time Label Axis)
                            timetableModel.setValueAt(
                                    new TaskSlot(task, type, displayStartTime, displayEndTime),
                                    row, dayIndex + 1
                            );
                            visibleRowCounter++;
                        }
                    }
                }

                currentDayStartMinutes = nextDayStartMinutes;
            }
        }

        timetableTable.repaint();
        updateNavigationButtons();
    }

    private static LocalTime minutesToLocalTime(int minutesOfDay) {
        int hours = minutesOfDay / 60;
        int minutes = minutesOfDay % 60;
        return LocalTime.of(hours, minutes);
    }

    // TOP PANE BUTTON LISTENERS

    private void openSettings() {
        SettingsUI settingsUI = new SettingsUI(this, slotInMinutes, lowerBound, upperBound);
        // If OK
        if (settingsUI.isConfirmed()) {
            // Pull Changes to Settings
            this.slotInMinutes = settingsUI.getSlotInMinutes();
            this.lowerBound = settingsUI.getLowerBound();
            this.upperBound = settingsUI.getUpperBound();

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
}