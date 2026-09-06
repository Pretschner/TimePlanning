package zhuangyan.timeplanning;

import com.formdev.flatlaf.FlatIntelliJLaf;
import zhuangyan.timeplanning.view.AuthenticationUI;

import javax.swing.*;

/** Launches the Swing app with FlatLaf styling and opens the login screen. */
public class Main {
    public static void main(String[] args) {
        try {
            FlatIntelliJLaf.setup();
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(AuthenticationUI::new);
    }
}
