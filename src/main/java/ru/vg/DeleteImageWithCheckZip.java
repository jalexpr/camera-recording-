package ru.vg;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static ru.vg.util.HelperPath.getOutDirPathImageForVideo;
import static ru.vg.util.HelperProperties.getCameraNameForDir;
import static ru.vg.util.HelperProperties.getCamerasName;
import static ru.vg.util.Time.currentDay;

public class DeleteImageWithCheckZip {
    public static void main(String[] args) throws IOException {
        runDeleteImageWithCheckZip();
    }

    public static void runDeleteImageWithCheckZip() throws IOException {
        for (String camNameInProperties : getCamerasName()) {
            String camName = getCameraNameForDir(camNameInProperties);
            File dirs = new File(getOutDirPathImageForVideo(camName, ""));
            for (File dir : dirs.listFiles()) {
                if (dir.getName().equals(currentDay())) {
                    continue;
                }

                File fileArchive = ZipDir.getFileArchiveByCamName(camName, dir.getName());
                if (!fileArchive.exists()) {
                    ZipDir.applyZip(camName, dir);
                    System.out.println("Заархивировал " + fileArchive.getAbsolutePath() + " original = " + dir.getAbsolutePath());
                }

                if (fileArchive.exists()) {
                    Arrays.asList(dir.listFiles()).forEach(File::delete);
                    dir.delete();
                } else {
                    System.out.println("Нет заархивированного " + fileArchive.getAbsolutePath());
                }
            }
        }
    }
}
