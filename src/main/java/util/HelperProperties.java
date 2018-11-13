package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class HelperProperties {
    private static final Logger log = LoggerFactory.getLogger(HelperPath.class);
    private volatile static ResourceBundle properties;

    public static synchronized ResourceBundle getProperties() {
        if (null == properties) {
            reReadProperties();
        }
        return properties;
    }

    public static synchronized void reReadProperties() {
        try {
            properties = new PropertyResourceBundle(new FileInputStream("src/main/resources/properties.properties"));
        } catch (IOException ex) {
            log.warn("I am not READING properties", ex);
        }
    }

    public static synchronized String[] getCamerasName() {
        return getProperties().getString("cam.name").split(";");
    }

    public static synchronized String getCameraNameForDir(String camNameInProperties) {
        return getProperties().getString("propt." + camNameInProperties + ".name");
    }
}
