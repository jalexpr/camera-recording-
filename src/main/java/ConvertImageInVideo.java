import util.HelperPath;
import video.save.SaveVideo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static util.HelperProperties.getCameraNameForDir;
import static util.HelperProperties.getCamerasName;
import static util.Time.currentDay;

public class ConvertImageInVideo {
    public static void main(String[] args) {
        for (String camNameInProperties : getCamerasName()) {
            convertImageInVideo(camNameInProperties);
        }
    }

    private static void convertImageInVideo(String camNameInProperties) {
        String camName = getCameraNameForDir(camNameInProperties);
        File dirs = new File(HelperPath.getOutDirPathImageForVideo(camName, ""));
        for(File dir : dirs.listFiles()) {
            if (dir.getName().equals(currentDay())) {
                continue;
            }

            List<File> files = Arrays.asList(dir.listFiles());
            files.sort((file1, file2) -> file1.getName().compareTo(file1.getName()));

            SaveVideo video = new SaveVideo(camName, "");
            for (File imageFile : files) {
                try {
                    BufferedImage image = ImageIO.read(imageFile);
                    video.writerImage(image);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//            imageFile.delete();
            }
            video.close();
//            dir.delete();
        }
    }
}
