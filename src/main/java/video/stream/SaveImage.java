package video.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class SaveImage implements ISave {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String FORMAT_DEFAULT = "PNG";
    private final String imageOutDirFor;
    private final String camName;

    public SaveImage(String imageOutDirFor, String camName) {
        imageOutDirFor += "/" + camName;
        File outDirFile = new File(imageOutDirFor);
        if (!outDirFile.exists()) {
            outDirFile.mkdirs();
        }

        this.imageOutDirFor = imageOutDirFor;
        this.camName = camName;
    }

    @Override
    public void writerImage(BufferedImage javaImage) {
        String nowTime = formatNowTime.format(Calendar.getInstance().getTime());
        String fileName = String.format("%s__%s.%s", camName, nowTime, FORMAT_DEFAULT.toLowerCase());

        File file = new File(imageOutDirFor, fileName);
        if (!file.exists()) {
            try {
                ImageIO.write(javaImage, FORMAT_DEFAULT, file);
                log.info("Save image. Camera name = " + camName);
            } catch (IOException ex) {
                log.warn("Not save image. Camera name = " + camName, ex);
            }
        }
    }

    @Override
    public void close() {
    }
}
