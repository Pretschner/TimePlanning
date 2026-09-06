package zhuangyan.timeplanning.view.renderers;

import zhuangyan.timeplanning.model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/** Table cell renderer: paints {@code TaskSlot} cells with task name/time and group color; grays the time-label column. */
public class TimetableRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        if (col == 0) {
            setBackground(new Color(240, 240, 240));
            setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
        if (value instanceof TaskSlot t) {
            if (t.type() == SlotType.NAME_DISPLAY) {
                setText(t.task().name());
            }
            else if (t.type() == SlotType.TIME_DISPLAY) {
                setText(t.taskStart().toString() + " -> " + t.taskEnd().toString());
            }
            else {
                setText("");
            }
            setBackground(ColorSelector.colorForGroup(t.task().group()));
            setToolTipText(t.task().name() + "\n" + t.task().group() + "\n" + t.taskStart().toString() + " -> " + t.taskEnd().toString());
            setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            setBackground(Color.WHITE);
            setText("");
        }
        return c;
    }
}