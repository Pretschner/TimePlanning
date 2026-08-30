package zhuangyan.timeplanning.view.renderers;

import java.awt.*;

public class ColorSelector {
    public static Color colorForGroup(String group) {
        int hash = group.hashCode();
        float hue = (Math.abs(hash) % 360) / 360f;
        return Color.getHSBColor(hue, 0.45f, 0.95f);
    }
}
