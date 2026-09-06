package zhuangyan.timeplanning.view.dialogs;

import zhuangyan.timeplanning.view.MainApplicationUI;
import zhuangyan.timeplanning.view.dialogs.panels.ButtonPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/** Abstract modal dialog skeleton: form panel + button bar, executes a callback with the constructed entity (or batch). */
public abstract class BaseDialog<T> {
    protected final JDialog dialog;
    protected final Consumer<T> consumer;
    protected final ButtonPanel buttonPanel;
    protected final JPanel formPanel;

    protected BaseDialog(MainApplicationUI parent, String title, boolean isBatch, JPanel formPanel, Consumer<T> consumer) {
        this.consumer = consumer;
        this.formPanel = formPanel;
        this.dialog = new JDialog(parent, title, true);
        this.buttonPanel = new ButtonPanel(isBatch);

        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.getRootPane().setDefaultButton(buttonPanel.getExecute());

        buttonPanel.getExecute().addActionListener(e -> onExecute());
        buttonPanel.getCancel().addActionListener(e -> dialog.dispose());
    }

    protected abstract void onExecute();

    protected void closeWithResult(T result) {
        dialog.dispose();
        if (result != null) {
            consumer.accept(result);
        }
    }

    protected void closeWithResults(List<T> results) {
        dialog.dispose();
        for (T result : results) {
            if (result != null) {
                consumer.accept(result);
            }
        }
    }

    public void show() {
        dialog.setVisible(true);
    }
}