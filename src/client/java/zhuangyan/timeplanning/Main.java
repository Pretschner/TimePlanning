package zhuangyan.timeplanning;

import com.formdev.flatlaf.FlatIntelliJLaf;
import zhuangyan.timeplanning.view.AuthenticationUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            FlatIntelliJLaf.setup();
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(AuthenticationUI::new);
    }
}
