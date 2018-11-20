import util.HelperPath;
import video.save.SaveVideo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static util.HelperProperties.getCameraNameForDir;
import static util.HelperProperties.getCamerasName;
import static util.Time.currentDay;
import static video.save.SaveVideo.FORMAT_DEFAULT;

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
        for(File dir : dirs.listFiles()) {
            if (dir.getName().equals(currentDay())) {
                continue;
            }

            String videoFileName = String.format("%s_%s", camName, dir.getName());
            if (dirVideo.contains(videoFileName)) {
                continue;
            }

            List<File> files = Arrays.asList(dir.listFiles());
            files.sort((file1, file2) -> file1.getName().compareTo(file1.getName()));

            SaveVideo video = new SaveVideo(camName, "", videoFileName);
            int count = 3;
            for (File imageFile : files) {
                if (++count % 2 == 0 || count < 3_500 || count > 14_804) {
                    try {
                        BufferedImage image = ImageIO.read(imageFile);
                        video.writerImage(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
//            imageFile.delete();
            }
            video.close();
//            dir.delete();
        }
    }

    private static List<String> getListVideoName(String camName) {
        List<String> listVideoName = new LinkedList<>();

        File dir = new File(HelperPath.getOutDirPathVideo(camName, ""));
        for(File file : dir.listFiles()) {
            String name = file.getName();
            if(name.endsWith(FORMAT_DEFAULT)) {
                listVideoName.add(file.getName().replace(FORMAT_DEFAULT, ""));
            }
        }

        return listVideoName;
    }
}
