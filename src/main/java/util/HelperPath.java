package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ResourceBundle;

import static util.Time.currentDay;

public class HelperPath {
    private static final Logger log = LoggerFactory.getLogger(HelperPath.class);

    private static ResourceBundle properties = ServiceUtil.getProperties();

    private static final String PATH_FOLDER_VIDEO = "/video";
    private static final String PATH_FOLDER_IMAGE = "/image";
    private static final String PATH_FOLDER_IMAGE_SHOT = "/imageShot";

    public static String getOutDirPathVideo(String camName) {
        return crateDir(camName, PATH_FOLDER_VIDEO);
    }

    public static String getOutDirPathImage(String camName) {
        return crateDir(camName, PATH_FOLDER_IMAGE);
    }

    public static String getOutDirPathImageShot(String camName) {
        return crateDir(camName, PATH_FOLDER_IMAGE_SHOT);
    }

    private static String crateDir(String camName, String folderMiddling) {
        String dir = properties.getString("path.save") + folderMiddling + "/" + camName + "/" + currentDay();
        log.info(String.format("Create dir path  = %s %S", dir, folderMiddling));
        mkdirs(dir);
        return dir;
    }

    public static void mkdirs(String dir) {
        File outDirFile = new File(dir);
        if (!outDirFile.exists()) {
            outDirFile.mkdirs();
        }
    }
}
