package util;

import java.util.ResourceBundle;

public class ServiceUtil {
    private static ResourceBundle properties;

    public static synchronized ResourceBundle getProperties() {
        if (null == properties) {
            properties = ResourceBundle.getBundle("properties");
        }
        return properties;
    }
}
