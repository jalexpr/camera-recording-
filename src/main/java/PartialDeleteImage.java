import util.HelperPath;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static util.HelperProperties.getCameraNameForDir;
import static util.Time.currentDay;

public class PartialDeleteImage {
    public static void main(String[] args) {
//        for (String camNameInProperties : getCamerasName()) {
//        convertImageInVideo("cam2");
//        }
    }

    protected static void partialDelete(String camNameInProperties) {
        String camName = getCameraNameForDir(camNameInProperties);
        File dirs = new File(HelperPath.getOutDirPathImageForVideo(camName, ""));
        for (File dir : dirs.listFiles()) {
            if (dir.getName().equals(currentDay())) {
                continue;
            }
            List<File> files = Arrays.asList(dir.listFiles());
            files.sort((file1, file2) -> file1.getName().compareTo(file1.getName()));

            int count = 3;
            for (File imageFile : files) {
                if (++count >= 3) {
                    count = 0;
                    continue;
                }
                imageFile.delete();
            }
        }
    }
}
