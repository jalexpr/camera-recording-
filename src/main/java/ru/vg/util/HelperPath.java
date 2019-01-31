package ru.vg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ResourceBundle;

import static ru.vg.util.Time.currentDay;

public class HelperPath {
    private static final Logger log = LoggerFactory.getLogger(HelperPath.class);

    private static ResourceBundle properties = HelperProperties.getProperties();

    private static final String PATH_FOLDER_VIDEO = "/video";
    private static final String PATH_FOLDER_IMAGE_FOR_VIDEO = "/imageForVideo";
    private static final String PATH_FOLDER_IMAGE = "/image";
    private static final String PATH_FOLDER_IMAGE_SHOT = "/imageShot";
    private static final String PATH_FOLDER_ARCHIVE = "/archive";

    public static String getOutDirPathVideo(String camName) {
        return crateDir(camName, PATH_FOLDER_VIDEO);
    }

    public static String getOutDirPathVideo(String camName, String subfolder) {
        return crateDir(camName, PATH_FOLDER_VIDEO, subfolder);
    }

    public static String getOutDirPathImageForVideo(String camName) {
        return crateDir(camName, PATH_FOLDER_IMAGE_FOR_VIDEO);
    }

    public static String getOutDirPathImageForVideo(String camName, String subfolder) {
        return crateDir(camName, PATH_FOLDER_IMAGE_FOR_VIDEO, subfolder);
    }

    public static String getOutDirPathImage(String camName) {
        return crateDir(camName, PATH_FOLDER_IMAGE);
    }

    public static String getOutDirPathImageShot(String camName) {
        return crateDir(camName, PATH_FOLDER_IMAGE_SHOT);
    }

    private static String crateDir(String camName, String folderMiddling) {
        return crateDir(camName, folderMiddling, currentDay());
    }

    private static String crateDir(String camName, String folderMiddling, String subfolder) {
        String dir = properties.getString("path.save") + folderMiddling + "/" + camName + "/" + subfolder;
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

    public static String getOutDirPathArchive(String camName) {
        return crateDir(camName, PATH_FOLDER_ARCHIVE, "");
    }
}
