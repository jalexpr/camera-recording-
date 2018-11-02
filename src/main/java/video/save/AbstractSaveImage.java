package video.save;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public abstract class AbstractSaveImage implements ISave {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String FORMAT_DEFAULT = "PNG";
    protected final String camName;

    public AbstractSaveImage(String camName) {
        this.camName = camName;
    }

    protected abstract String getImageOutDir();

    @Override
    public void writerImage(BufferedImage javaImage) {
        String nowTime = formatNowTime.format(Calendar.getInstance().getTime());
        String fileName = String.format("%s__%s.%s", camName, nowTime, FORMAT_DEFAULT.toLowerCase());

        File file = new File(getImageOutDir(), fileName);
        if (!file.exists()) {
            try {
                ImageIO.write(javaImage, FORMAT_DEFAULT, file);
                log.info("Save image.");
            } catch (IOException ex) {
                log.warn("Not save image.", ex);
            }
        }
    }

    @Override
    public void close() {
    }
}
