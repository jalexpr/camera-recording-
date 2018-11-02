package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ServiceUtil {
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
}
