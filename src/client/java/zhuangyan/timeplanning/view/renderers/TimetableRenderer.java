package zhuangyan.timeplanning.view.renderers;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/** Table cell renderer: paints {@code TimetableCell} cells with task name/time and group color; grays the time-label column. */
public class TimetableRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        if (col == 0) {
            setBackground(new Color(240, 240, 240));
            setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
        if (value instanceof TimetableCell cell) {
            switch (cell.type()) {
                case NAME_DISPLAY -> setText(cell.task().name());
                case TIME_DISPLAY -> setText(cell.start() + " -> " + cell.end());
                case BACKGROUND -> setText("");
            }
            setBackground(ColorSelector.colorForGroup(cell.task().group()));
            setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            setBackground(Color.WHITE);
            setText("");
        }
        return c;
    }
}