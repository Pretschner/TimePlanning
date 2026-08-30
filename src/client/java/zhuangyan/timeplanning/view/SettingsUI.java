package zhuangyan.timeplanning.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;

public class SettingsUI extends JDialog implements ActionListener {
    /**
     * Settings for Schedule Generation and Representation
     */
    private JComboBox<Integer> slotBox;
    private JComboBox<String> strategyBox;
    private JTextField lbField, ubField;
    private JButton ok, cancel;

    // Confirmed Pattern and Pulling Data Transfer
    private boolean confirmed;
    private int slotInMinutes;
    // TODO: Have Groups configurable.
    private LocalTime lowerBound, upperBound;

    public SettingsUI(Frame owner, int slotInMinutes, LocalTime lowerBound, LocalTime upperBound) {
        super(owner, "TimePlanning - Settings", true);

        confirmed = false;
        this.slotInMinutes = slotInMinutes != 0 && slotInMinutes % 15 == 0 ? slotInMinutes : 15;
        this.lowerBound = lowerBound != null ? lowerBound : LocalTime.parse("08:00");
        this.upperBound = upperBound != null ? upperBound : LocalTime.parse("23:00");

        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(ok);
        setTitle("Time Planning - Settings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private JPanel createFormPanel () {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        formPanel.add(titleLabel, gbc);

        // ComboBoxes
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel slotLength = new JLabel("Slot Length (min):");
        formPanel.add(slotLength, gbc);

        gbc.gridx = 1;
        slotBox = new JComboBox<>(new Integer[]{15, 30, 60});
        slotBox.setSelectedItem(slotInMinutes);
        formPanel.add(slotBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;

        // TextFields
        JLabel lowerBound = new JLabel("Lower Bound:");
        formPanel.add(lowerBound, gbc);

        gbc.gridx = 1;
        lbField = new JTextField(this.lowerBound.toString());
        formPanel.add(lbField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel upperBound = new JLabel("Upper Bound:");
        formPanel.add(upperBound, gbc);

        gbc.gridx = 1;
        ubField = new JTextField(this.upperBound.toString());
        formPanel.add(ubField, gbc);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ok = new JButton("OK");
        ok.addActionListener(this);
        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        return buttonPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ok.equals(e.getSource())) {
            // Parsing Values
            slotInMinutes = (int) slotBox.getSelectedItem();

            try {
                lowerBound = LocalTime.parse(lbField.getText());
            }
            catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Time format: HH:MM (e.g. 09:00)");
                lbField.requestFocus();
                return;
            }

            try {
                upperBound = LocalTime.parse(ubField.getText());
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Time format: HH:MM (e.g. 09:00)");
                ubField.requestFocus();
                return;
            }

            if (lowerBound.isAfter(upperBound) || lowerBound.equals(upperBound)) {
                JOptionPane.showMessageDialog(this, "Lower Bound must be before Upper Bound.");
                lbField.requestFocus();
                return;
            }

            confirmed = true;
        }

        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getSlotInMinutes() {
        return slotInMinutes;
    }

    public LocalTime getLowerBound() {
        return lowerBound;
    }

    public LocalTime getUpperBound() {
        return upperBound;
    }
}
