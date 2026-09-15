package zhuangyan.timeplanning.view;

import javax.swing.*;

public class IconUtil {
    public static ImageIcon createImageIcon(String path,
                                        String description) {
        java.net.URL imgURL = IconUtil.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
