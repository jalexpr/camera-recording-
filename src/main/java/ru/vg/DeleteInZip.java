package ru.vg;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.util.zip.Deflater.BEST_SPEED;

public class DeleteInZip {
    public static void main(String[] args) throws IOException {
        File dir = new File(args[0]);
        Path newDir = Paths.get(args[1]);
        if (dir.exists() && dir.isDirectory()) {
            for (File zip : dir.listFiles()) {
                if (zip.getName().contains(".rar")) {
                    ZipInputStream zipFile = new ZipInputStream(new FileInputStream(zip));
                    ZipOutputStream newZip = createZOS(newDir.resolve(zip.getName()).toAbsolutePath());

                    int value = 0;
                    ZipEntry entry;
                    while ((entry = zipFile.getNextEntry()) != null) {
                        int nextValue = Integer.valueOf(entry.getName().substring(13, 15));
                        if (nextValue > value && nextValue - value >= 30 ||
                                nextValue < value && nextValue - value <= 30) {
                            addZipFile(newZip, zipFile, entry);
                            value = nextValue;
                        }
                    }
                    newZip.closeEntry();
                    newZip.close();
                    zipFile.close();
                }
            }
        }
    }

    private static ZipOutputStream createZOS(Path path) throws FileNotFoundException {
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(path.toString()));
        zout.setLevel(BEST_SPEED);
        return zout;
    }

    private static void addZipFile(ZipOutputStream zout, ZipInputStream zipFile, ZipEntry file) throws IOException {
        zout.putNextEntry(new ZipEntry(file.getName()));

        byte[] buffer = new byte[4048];
        int length;
        while((length = zipFile.read(buffer)) > 0) {
            zout.write(buffer, 0, length);
        }
    }
}
