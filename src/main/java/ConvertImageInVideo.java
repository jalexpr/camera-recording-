import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HelperPath;
import video.save.SaveVideo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static util.HelperProperties.getCameraNameForDir;
import static util.HelperProperties.getCamerasName;
import static util.Time.currentDay;
import static video.save.SaveVideo.FORMAT_VIDEO_DEFAULT;

public class ConvertImageInVideo {
    public static void main(String[] args) {
        for (String camNameInProperties : getCamerasName()) {
            convertImageInVideo(camNameInProperties);
        }
    }

    private static void convertImageInVideo(String camNameInProperties) {
        String camName = getCameraNameForDir(camNameInProperties);
        File dirs = new File(HelperPath.getOutDirPathImageForVideo(camName, ""));
        List<String> dirVideo = getListVideoName(camName);
        for(File dir : Objects.requireNonNull(dirs.listFiles())) {
            if (dir.getName().equals(currentDay())) {
                continue;
            }

            Logger logger = LoggerFactory.getLogger(ConvertImageInVideo.class);
            logger.info("Convert dir = " + dir);

            String videoFileName = String.format("%s_%s", camName, dir.getName());
            if (dirVideo.contains(videoFileName)) {
                continue;
            }

            List<File> files = Arrays.asList(dir.listFiles());
            files.sort((file1, file2) -> file1.getName().compareTo(file1.getName()));

            SaveVideo video = new SaveVideo(camName, "", videoFileName);
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
                video.close();
            }
//            dir.delete();
        }
    }

    private static List<String> getListVideoName(String camName) {
        List<String> listVideoName = new LinkedList<>();

        File dir = new File(HelperPath.getOutDirPathVideo(camName, ""));
        for(File file : dir.listFiles()) {
            String name = file.getName();
            if(name.endsWith(FORMAT_VIDEO_DEFAULT)) {
                listVideoName.add(file.getName().replace(FORMAT_VIDEO_DEFAULT, ""));
            }
        }

        return listVideoName;
    }
}
