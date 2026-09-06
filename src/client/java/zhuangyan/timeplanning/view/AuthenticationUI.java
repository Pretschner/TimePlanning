package zhuangyan.timeplanning.view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import org.springframework.web.client.RestClientException;
import zhuangyan.timeplanning.controller.AuthenticationController;
import zhuangyan.timeplanning.model.Credentials;

import javax.swing.*;
import java.awt.*;

/** Login/signup screen with card layout switching; on success hands off to {@code MainApplicationUI}. */
public class AuthenticationUI extends JFrame {
    private final AuthenticationController authController;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    // Fields for Login
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JButton loginButton;

    // Fields for Signup
    private JTextField signupUsernameField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmField;
    private JButton signupButton;

    public AuthenticationUI() {
        this.authController = new AuthenticationController();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        setTitle("Time Planning - Startup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // Build the two screens
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createSignupPanel(), "SIGNUP");

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        loginUsernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(loginUsernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        loginPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(loginPasswordField, gbc);

        // Login Button (default button)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Log In");
        loginButton.setPreferredSize(new Dimension(120, 35));
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> performLogin());

        // Switch to Signup
        gbc.gridy = 4;
        JLabel switchToSignup = new JLabel("Don't have an account? Sign Up", SwingConstants.CENTER);
        switchToSignup.setForeground(Color.BLUE.darker());
        switchToSignup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchToSignup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(mainPanel, "SIGNUP");
                clearSignupFields(); // Fresh start
            }
        });
        panel.add(switchToSignup, gbc);

        // Enter = Login
        getRootPane().setDefaultButton(loginButton);

        return panel;
    }

    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        signupUsernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(signupUsernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        signupPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(signupPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Confirm:"), gbc);
        signupConfirmField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(signupConfirmField, gbc);

        // Signup Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        signupButton = new JButton("Create Account");
        signupButton.setPreferredSize(new Dimension(140, 35));
        panel.add(signupButton, gbc);

        signupButton.addActionListener(e -> performSignup());

        // Switch to Log in
        gbc.gridy = 5;
        JLabel switchToLogin = new JLabel("Already have an account? Log In", SwingConstants.CENTER);
        switchToLogin.setForeground(Color.BLUE.darker());
        switchToLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchToLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(mainPanel, "LOGIN");
                clearLoginFields();
            }
        });
        panel.add(switchToLogin, gbc);

        return panel;
    }


    private void performLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginButton.setText("Logging in...");

        Credentials credentials = new Credentials(username, password); // Assuming constructor exists

        new SwingWorker<Void, Void>() {
            private String errorMessage = null;

            @Override
            protected Void doInBackground() {
                try {
                    authController.login(credentials);
                } catch (RestClientException e) {
                    errorMessage = extractErrorMessage(e);
                }
                return null;
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Log In");

                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(AuthenticationUI.this, errorMessage, "Login Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Success -> MainApplicationUI
                SwingUtilities.invokeLater(() -> new MainApplicationUI().setVisible(true));
                dispose();
            }
        }.execute();
    }

    private void performSignup() {
        String username = signupUsernameField.getText().trim();
        String password = new String(signupPasswordField.getPassword());
        String confirm = new String(signupConfirmField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        signupButton.setEnabled(false);
        signupButton.setText("Creating...");

        Credentials credentials = new Credentials(username, password);

        new SwingWorker<Void, Void>() {
            private String errorMessage = null;

            @Override
            protected Void doInBackground() {
                try {
                    authController.createAccount(credentials);
                } catch (RestClientException e) {
                    errorMessage = extractErrorMessage(e);
                }
                return null;
            }

            @Override
            protected void done() {
                signupButton.setEnabled(true);
                signupButton.setText("Create Account");

                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(AuthenticationUI.this, errorMessage, "Signup Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Success -> Back to Login
                JOptionPane.showMessageDialog(AuthenticationUI.this, "Account created! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "LOGIN");
                clearLoginFields();
                loginUsernameField.setText(username);
            }
        }.execute();
    }

    private void clearLoginFields() {
        loginUsernameField.setText("");
        loginPasswordField.setText("");
    }

    private void clearSignupFields() {
        signupUsernameField.setText("");
        signupPasswordField.setText("");
        signupConfirmField.setText("");
    }

    // Basic error extraction (catches Spring's RestClient exceptions)
    private String extractErrorMessage(RestClientException e) {
        String msg = e.getMessage();
        if (msg != null && msg.contains("400")) return "Invalid credentials or bad request.";
        if (msg != null && msg.contains("409")) return "Username already taken.";
        if (msg != null && msg.contains("500")) return "Server error. Try again later.";
        return msg != null ? msg : "Network error. Check your connection.";
    }
}