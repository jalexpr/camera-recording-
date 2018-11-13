package video.save;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HelperPath;

import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SaveVideo implements ISave {
    private final Logger log = LoggerFactory.getLogger(getClass());
    public static final String FORMAT_DEFAULT = ".mp4";
    public static final Integer WIDTH_DEFAULT = 1280;
    public static final Integer HEIGHT_DEFAULT = 720;

    private volatile IMediaWriter writer;
    private final String camName;
    private int count;

    public SaveVideo(String camName) {
        this.camName = camName;
        reInit(HelperPath.getOutDirPathVideo(camName));
    }

    public SaveVideo(String camName, String subfolder) {
        this.camName = camName;
        reInit(HelperPath.getOutDirPathVideo(camName, subfolder));
    }

    public void reInit(String outDir) {
        try {
            close();

            String nowTime = formatNowTime.format(Calendar.getInstance().getTime());
            String smallFileName = camName + "__" + nowTime;

            writer = ToolFactory.makeWriter(outDir + "/" + smallFileName + FORMAT_DEFAULT);
            writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, IRational.make(1), WIDTH_DEFAULT, HEIGHT_DEFAULT);
            count = 0;
        } catch (Throwable ex) {
            log.error("SaveVideo stopped!", ex);
        }
    }

    @Override
    public void writerImage(BufferedImage bgrScreen) {
        try {
            bgrScreen = convertToType(bgrScreen, BufferedImage.TYPE_3BYTE_BGR);
            writer.encodeVideo(0, bgrScreen, count++, TimeUnit.SECONDS);
            flush();
        } catch (NullPointerException | IllegalArgumentException ex) {
            log.error("", ex);
        } catch (Throwable ex) {
            log.error("SaveVideo stopped!", ex);
        }
    }

    private BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        try {
            BufferedImage image;

            if (sourceImage.getType() == targetType) {
                image = sourceImage;
            } else {
                image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
                image.getGraphics().drawImage(sourceImage, 0, 0, null);
            }
            return image;
        } catch (Throwable ex) {
            log.error("Not converted Image", ex);
        }
        return null;
    }

    @Override
    public void close() {
        if (writer != null) {
            String oldUrl = writer.getUrl();
            try {
                writer.close();
                log.info(String.format("I am CLOSE VIDEO camName = %s", camName));
            } catch (Throwable ex) {
                log.error(String.format("CamName = %s not save video url = %s", camName, oldUrl), ex);
            }
        }
    }

    private void flush() {
        try {
            writer.flush();
        }  catch (Throwable ex) {
            log.error("SaveVideo not flush!", ex);
        }
    }
}