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
    public static final String EXPANSION_ARCHIVE = ".rar";

    public static void main(String[] args) throws IOException {
        runZip();
    }

    public static void runZip() throws IOException {
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

            String dirName = getDirNameByCamName(camName, dir.getName());
            if (dirArchive.contains(dirName)) {
                continue;
            }

            applyZip(camName, dir);
//            dir.delete();
        }
    }

    public static void applyZip(String camName, File originalDir) throws IOException {
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(getFileArchiveByCamName(camName, originalDir.getName())));
        zout.setLevel(BEST_SPEED);

        if(originalDir.isDirectory()) {
            for (File file : Objects.requireNonNull(originalDir.listFiles())) {
                addZipFile(zout, file);
            }
        } else {
            addZipFile(zout, originalDir);
        }
        zout.close();
    }

    public static File getFileArchiveByCamName(String camName, String originalDir) {
        return new File(getOutDirPathArchive(camName) + getDirNameByCamName(camName, originalDir) + ZipDir.EXPANSION_ARCHIVE);
    }

    private static String getDirNameByCamName(String camName, String originalDir) {
        return String.format("%s_%s", camName, originalDir);
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

    private static List<String> getListArchiveFileName(String camName) {
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
