package ru.vg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vg.util.HelperPath;
import ru.vg.util.HelperProperties;
import ru.vg.video.save.SaveVideo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static ru.vg.util.HelperPath.getOutDirPathImageForVideo;
import static ru.vg.util.HelperProperties.*;
import static ru.vg.util.Time.currentDay;
import static ru.vg.video.save.SaveVideo.FORMAT_VIDEO_DEFAULT;

public class ConvertImageInVideo {
    private static Logger log = LoggerFactory.getLogger(ConvertImageInVideo.class);

    private static ResourceBundle properties = HelperProperties.getProperties();

    public static void main(String[] args) {
        runConvert();
    }

    public static void runConvert() {
        log.info("Начиную конвертацию");
        for (String camNameInProperties : getCamerasName()) {
            String nameDirByCame = getCameraNameForDir(camNameInProperties);
            convertImageInVideo(camNameInProperties, getOutDirPathImageForVideo(nameDirByCame, ""));
//            convertImageInVideo(camNameInProperties, getOutDirPathImage(nameDirByCame, ""));
        }
    }

    private static void convertImageInVideo(String camNameInProperties, String outDirPathImageForVideo) {
        log.info("camNameInProperties = {} | outDirPathImageForVideo  = {} ", camNameInProperties, outDirPathImageForVideo);
        String camName = getCameraNameForDir(camNameInProperties);
        File dirs = new File(outDirPathImageForVideo);
        List<String> dirVideo = getListVideoName(camName);
        for (File dir : dirs.listFiles()) {
            if (isConvertOnlyCurrentDay() && !dir.getName().equals(currentDay())) {
                continue;
            }

            log.info("Convert dir = " + dir);

            String homes;
            if (camName.equals("cam_4")) {
                homes = " дома 13.2 и 13.1";
            } else if (camName.equals("cam_7")) {
                homes = " дома 13, 12 и 5";
            } else {
                homes = " ";
            }

            String videoFileName =  dir.getName() + " ЖК Видный город " + homes;
            if (dirVideo.contains(videoFileName)) {
                continue;
            }

            List<File> files = Arrays.asList(dir.listFiles());
            files.sort((file1, file2) -> file1.getName().compareTo(file1.getName()));

            SaveVideo video = new SaveVideo(camName, "", videoFileName);
            int count = 3;
            try {
                for (File imageFile : files) {
//                    if (++count % 2 == 0 || count < 3_500 || count > 14_804) {
                        try {
                            BufferedImage image = ImageIO.read(imageFile);
                            video.writerImage(image);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex.getMessage() + " File name = " + imageFile.getAbsolutePath());
                        }
//                    }
//            imageFile.delete();
                }
            } finally {
                video.close();
            }
//            dir.delete();
        }
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
