import util.HelperPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.zip.Deflater.BEST_SPEED;
import static util.HelperPath.getOutDirPathArchive;
import static util.HelperPath.getOutDirPathImageForVideo;
import static util.HelperProperties.getCameraNameForDir;
import static util.HelperProperties.getCamerasName;
import static util.Time.currentDay;

public class ZipDir {
    private static final String EXPANSION_ARCHIVE = ".rar";

    public static void main(String[] args) throws IOException {
        for (String camNameInProperties : getCamerasName()) {
            zipDir(camNameInProperties);
        }
    }

    private static void zipDir(String camNameInProperties) throws IOException {
        String camName = getCameraNameForDir(camNameInProperties);
        File dirs = new File(getOutDirPathImageForVideo(camName, ""));
        List<String> dirArchive = getListArchiveFileName(camName);
        for(File dir : dirs.listFiles()) {
            if (dir.getName().equals(currentDay())) {
                continue;
            }

            String dirName = String.format("%s_%s", camName, dir.getName());
            if (dirArchive.contains(dirName)) {
                continue;
            }

            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(getOutDirPathArchive(camName) + dirName + EXPANSION_ARCHIVE));
            zout.setLevel(BEST_SPEED);

            if(dir.isDirectory()) {
                for (File file : Objects.requireNonNull(dir.listFiles())) {
                    addZipFile(zout, file);
                }
            } else {
                addZipFile(zout, dir);
            }
//            dir.delete();
        }
    }

    private static void addZipFile(ZipOutputStream zout, File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        zout.putNextEntry(new ZipEntry(file.getName()));

        byte[] buffer = new byte[4048];
        int length;
        while((length = fis.read(buffer)) > 0) {
            zout.write(buffer, 0, length);
        }
        zout.closeEntry();
        fis.close();
    }

    public static List<String> getListArchiveFileName(String camName) {
        List<String> listVideoName = new LinkedList<>();

        File dir = new File(HelperPath.getOutDirPathArchive(camName));
        for(File file : dir.listFiles()) {
            String name = file.getName();
            if (name.endsWith(EXPANSION_ARCHIVE)) {
                listVideoName.add(file.getName().replace(EXPANSION_ARCHIVE, ""));
            } else if (file.isDirectory()) {
                listVideoName.add(file.getName());
            }
        }

        return listVideoName;
    }
}
