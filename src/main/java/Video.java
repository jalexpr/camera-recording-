import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class Video {
    public static final String FORMAT_DEFAULT = ".mp4";
    public static final Integer WIDTH_DEFAULT = 1280;
    public static final Integer HEIGHT_DEFAULT = 720;

    public static  String outDir;

    private String smallFileName;
    private IMediaWriter writer;
    private long startTime;

    public Video(String url, String smallFileName) {
        this.smallFileName = smallFileName;
        startTime = System.nanoTime();

        init();
    }

    private void init() {
        File outDirFile = new File(outDir);
        if (!outDirFile.exists()) {
            outDirFile.mkdirs();
        }

        writer = ToolFactory.makeWriter(outDir + );
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, WIDTH_DEFAULT, HEIGHT_DEFAULT);
    }

    private void writerImage(BufferedImage bgrScreen) {
            bgrScreen = convertToType(bgrScreen, BufferedImage.TYPE_3BYTE_BGR);
            writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime,
                    TimeUnit.NANOSECONDS);
            writer.flush();
    }

    public void close() {
        writer.close();
    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;

        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } else {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }
}