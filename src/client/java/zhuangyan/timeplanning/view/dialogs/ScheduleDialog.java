package zhuangyan.timeplanning.view.dialogs;

import zhuangyan.timeplanning.model.ScheduleConfig;
import zhuangyan.timeplanning.model.Strategy;
import zhuangyan.timeplanning.view.MainApplicationUI;
import zhuangyan.timeplanning.view.dialogs.panels.ButtonPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScheduleDialog {
    public static void show(MainApplicationUI parent, int slotInMinutes, Consumer<ScheduleConfig> schedulesConsumer) {
        JDialog dialog = new JDialog(parent, "Generate Timetable", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Stored Solutions
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Stored Solutions:"), gbc);
        gbc.gridx = 1;
        JComboBox<Integer> solutions = new JComboBox<>(new Integer[]{1,2,3,4,5});
        solutions.setSelectedItem(3);
        dialog.add(solutions, gbc);

        // Search Time
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Search Time (sec.):"), gbc);
        gbc.gridx = 1;
        JComboBox<Integer> time = new JComboBox<>(new Integer[]{5,10,15,20,25});
        time.setSelectedItem(15);
        dialog.add(time, gbc);

        // Combined Checkbox
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Strategy:"), gbc);
        gbc.gridx = 1;
        JCheckBox combine = new JCheckBox("Combine Strategies");
        dialog.add(combine, gbc);

        // Strategy Card Panel
        JPanel cardPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        final List<JRadioButton> radioButtons = new ArrayList<>();
        final List<JSpinner> spinners = new ArrayList<>();
        ButtonGroup group = new ButtonGroup();

        Strategy[] strategies = {Strategy.Early_Finish, Strategy.Flow_State,
                Strategy.Grouped_Leisure, Strategy.Memorizable_Schedule};
        String[] descriptions = {
                "Avoid late night work and get to rest sooner on average.",
                "Achieve a flow state of mind by grouping similar tasks together.",
                "Get the most out of your free time by grouping it into larger chunks.",
                "Make your schedule memorizable by having similar start times across the week."
        };

        for (int i = 0; i < strategies.length; i++) {
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            card.setBackground(Color.WHITE);

            // Radio Button – hidden when combined
            JRadioButton rb = new JRadioButton();
            rb.setBackground(Color.WHITE);
            group.add(rb);
            radioButtons.add(rb);

            // Label w. Name and Description
            int wrapWidth = 150;
            JLabel lbl = new JLabel("<html><div style='width:" + wrapWidth + "px;'><b>" + strategies[i].name().replace("_", " ") +
                    "</b><br><font size='2'>" + descriptions[i] + "</font></div></html>");
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

            // Spinner – hidden when not combined
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
            spinner.setPreferredSize(new Dimension(50, 25));
            spinner.setVisible(false);
            spinners.add(spinner);

            // Assemble card
            card.add(rb, BorderLayout.WEST);
            card.add(lbl, BorderLayout.CENTER);
            card.add(spinner, BorderLayout.EAST);

            // Click on card toggles the radio button (if not combined)
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!combine.isSelected()) {
                        rb.setSelected(true);
                    }
                }
            });

            cardPanel.add(card);
        }

        // Combine checkbox logic
        combine.addActionListener(e -> {
            boolean on = combine.isSelected();
            for (int i = 0; i < radioButtons.size(); i++) {
                radioButtons.get(i).setVisible(!on);
                spinners.get(i).setVisible(on);
                if (on) radioButtons.get(i).setSelected(false);
            }
            // Resize Dialog if necessary
            dialog.pack();
        });

        // Button Panel
        ButtonPanel buttonPanel = new ButtonPanel(true);

        // Assemble Dialog with Card and Button Panel
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        dialog.add(cardPanel, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.getRootPane().setDefaultButton(buttonPanel.getExecute());

        // Generate Action
        buttonPanel.getExecute().addActionListener(e -> {
            int solutionCount = (int) solutions.getSelectedItem();
            int searchTime = (int) time.getSelectedItem();

            ScheduleConfig config;
            List<Strategy> strategyList;
            if (combine.isSelected()) {

                strategyList = new ArrayList<>();
                List<Integer> weights = spinners.stream().map(s -> (int)s.getValue()).toList();

                for (int i = 0; i < weights.size(); i++) {
                    Strategy strategy = switch(i) {
                        case 0 -> Strategy.Early_Finish;
                        case 1 -> Strategy.Flow_State;
                        case 2 -> Strategy.Grouped_Leisure;
                        case 3 -> Strategy.Memorizable_Schedule;
                        default -> null;
                    };
                    if (strategy == null) continue;
                    for (int j = 0; j < weights.get(i); j++) {
                        strategyList.add(strategy);
                    }
                }

                for (var s : strategyList) {
                    System.out.println(s);
                }
            } else {
                Strategy selected = null;
                for (int i = 0; i < radioButtons.size(); i++) {
                    if (radioButtons.get(i).isSelected()) {
                        selected = strategies[i];
                        break;
                    }
                }
                if (selected == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a strategy.");
                    return;
                }
                strategyList = List.of(selected);
            }

            config = new ScheduleConfig(slotInMinutes, strategyList, solutionCount, searchTime);

            dialog.dispose();
            schedulesConsumer.accept(config);
        });

        buttonPanel.getCancel().addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}
