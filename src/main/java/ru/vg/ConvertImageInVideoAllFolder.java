package ru.vg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vg.util.HelperPath;
import ru.vg.video.save.SaveVideo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static ru.vg.util.HelperProperties.getCameraNameForDir;
import static ru.vg.util.HelperProperties.getCamerasName;
import static ru.vg.video.save.SaveVideo.FORMAT_VIDEO_DEFAULT;

public class ConvertImageInVideoAllFolder {
    private static Logger logger = LoggerFactory.getLogger(ConvertImageInVideoAllFolder.class);
    public static void main(String[] args) {
        runConvert();
    }

    public static void runConvert() {
        for (String camNameInProperties : getCamerasName()) {
            convertImageInVideo(camNameInProperties);
        }
    }

    private static void convertImageInVideo(String camNameInProperties) {
        String camName = getCameraNameForDir(camNameInProperties);
        File baseDir = new File(HelperPath.getOutDirPathImageForVideo(camName, ""));
        List<String> dirVideo = getListVideoName(camName);
        File[] dirs = baseDir.listFiles();
        if (dirs == null || dirs.length == 0) {
            return;
        }

        String homes;
        if (camName.equals("cam_4")) {
            homes = " дома 13.2 и 13.1";
        } else if (camName.equals("cam_7")) {
            homes = " дома 13, 12 и 5";
        } else {
            homes = " ";
        }

        String nameVideo = new StringBuilder()
                .append(dirs[0].getName())
                .append("-")
                .append(dirs[dirs.length - 1].getName().substring(8))
                .append(" ЖК Видный город")
                .append(homes).toString();
        nameVideo = nameVideo.replace("_", ".");

        SaveVideo video = new SaveVideo(camName, "", nameVideo);
        for (File dir : dirs) {
//            if (dir.getName().equals(currentDay())) {
//                continue;
//            }
            logger.info("Convert dir = " + dir);

            String videoFileName = String.format("%s_%s", camName, dir.getName());
            if (dirVideo.contains(videoFileName)) {
                continue;
            }

            List<File> files = Arrays.asList(dir.listFiles());
            files.sort((file1, file2) -> file1.getName().compareTo(file1.getName()));

            int count = 3;
            try {
                for (File imageFile : files) {
                    if (++count % 2 == 0 || count < 3_500 || count > 14_804) {
                        try {
                            BufferedImage image = ImageIO.read(imageFile);
                            video.writerImage(image);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex.getMessage() + " File name = " + imageFile.getAbsolutePath());
                        }
                    }
//            imageFile.delete();
                }
            } finally {
//                video.close();
            }
//            dir.delete();
        }
        video.close();
    }

    private static List<String> getListVideoName(String camName) {
        List<String> listVideoName = new LinkedList<>();

        File dir = new File(HelperPath.getOutDirPathVideo(camName, ""));
        for (File file : dir.listFiles()) {
            String name = file.getName();
            if (name.endsWith(FORMAT_VIDEO_DEFAULT)) {
                listVideoName.add(file.getName().replace(FORMAT_VIDEO_DEFAULT, ""));
            }
        }

        return listVideoName;
    }
}
