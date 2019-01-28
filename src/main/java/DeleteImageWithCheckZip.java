import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static util.HelperPath.getOutDirPathImageForVideo;
import static util.HelperProperties.getCameraNameForDir;
import static util.HelperProperties.getCamerasName;
import static util.Time.currentDay;

public class DeleteImageWithCheckZip {
    public static void main(String[] args) throws IOException {
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

    //    private static is
    private static void partialDelete(String camNameInProperties) {
        String camName = getCameraNameForDir(camNameInProperties);
        File dirs = new File(getOutDirPathImageForVideo(camName, ""));
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
