package zhuangyan.timeplanning.view;

import zhuangyan.timeplanning.controller.*;
import zhuangyan.timeplanning.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainApplicationUI extends JFrame {
    /**
     * Main UI handling CRUD operations for Tasks and Constraints and Schedule Generation
     */
    // Controller Access
    private final ControllerCaller controllerCaller;

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

    // Timetable navigation
    private JLabel scheduleNavLabel;
    private JButton prevScheduleButton;
    private JButton nextScheduleButton;

    // Bottom buttons
    private JButton generateBtn;
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    // Default Config for Schedule Generation / Rendering
    private int slotInMinutes = 15;


    public MainApplicationUI() {
        initUI();
        this.controllerCaller = new ControllerCaller(this);
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
        settingsBtn.addActionListener(e -> {
            // TODO
        });
        JButton helpBtn = new JButton("Help");
        helpBtn.addActionListener(e -> {
            // TODO
        });
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            // TODO
        });
        topRightPanel.add(settingsBtn);
        topRightPanel.add(helpBtn);
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
            // TODO
        });

        addBtn = new JButton("Add");
        addBtn.addActionListener(e -> {
            // TODO
        });

        editBtn = new JButton("Edit");
        editBtn.setEnabled(false);
        editBtn.addActionListener(e -> {
            // TODO
        });

        deleteBtn = new JButton("Delete");
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(e ->  {
            // TODO
        });

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 28));

        bottomPanel.add(generateBtn);
        bottomPanel.add(sep);
        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(deleteBtn);

        // Assemble
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        tabbedPane.addChangeListener(e -> {
            // TODO
        });
    }

    // TAB PANELS

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        taskList.setVisibleRowCount(-1);
        taskList.setBackground(Color.WHITE);

        taskList.addListSelectionListener(e -> {
            // TODO
        });

        panel.add(new JScrollPane(taskList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createConstraintPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        constraintListModel = new DefaultListModel<>();
        constraintList = new JList<>(constraintListModel);
        constraintList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        constraintList.setVisibleRowCount(-1);
        constraintList.setBackground(Color.WHITE);

        constraintList.addListSelectionListener(e -> {
            // TODO
        });

        panel.add(new JScrollPane(constraintList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table with Dummy Model
        timetableTable = new JTable(new DefaultTableModel(0, 0));
        timetableTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(timetableTable);

        // Navigation label
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        scheduleNavLabel = new JLabel("Schedule 1/1");
        navPanel.add(scheduleNavLabel);

        // Arrow buttons
        prevScheduleButton = new JButton("<");
        prevScheduleButton.setFont(prevScheduleButton.getFont().deriveFont(18f));
        prevScheduleButton.setPreferredSize(new Dimension(50, 60));
        prevScheduleButton.addActionListener(e -> {
            // TODO
        });

        nextScheduleButton = new JButton(">");
        nextScheduleButton.setFont(nextScheduleButton.getFont().deriveFont(18f));
        nextScheduleButton.setPreferredSize(new Dimension(50, 60));
        nextScheduleButton.addActionListener(e -> {
            // TODO
        });

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(prevScheduleButton, BorderLayout.WEST);
        centerWrapper.add(scrollPane, BorderLayout.CENTER);
        centerWrapper.add(nextScheduleButton, BorderLayout.EAST);

        // Assembling
        panel.add(navPanel, BorderLayout.NORTH);
        panel.add(centerWrapper, BorderLayout.CENTER);

        return panel;
    }
}