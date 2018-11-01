package video.stream;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SaveVideo implements ISave {
    public static final String FORMAT_DEFAULT = ".mp4";
    public static final Integer WIDTH_DEFAULT = 1280;
    public static final Integer HEIGHT_DEFAULT = 720;

    private final IMediaWriter writer;
    private int count = 0;

    public SaveVideo(String outDir, String camName) {
        String nowTime = formatNowTime.format(Calendar.getInstance().getTime());
        String smallFileName = camName + "__" + nowTime;

        File outDirFile = new File(outDir);
        if (!outDirFile.exists()) {
            outDirFile.mkdirs();
        }

        writer = ToolFactory.makeWriter(outDir + "/" + smallFileName + FORMAT_DEFAULT);
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, IRational.make(1), WIDTH_DEFAULT, HEIGHT_DEFAULT);
    }

    @Override
    public void writerImage(BufferedImage bgrScreen) {
        bgrScreen = convertToType(bgrScreen, BufferedImage.TYPE_3BYTE_BGR);
        writer.encodeVideo(0, bgrScreen, count++, TimeUnit.SECONDS);
        writer.flush();
    }

    private BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;

        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } else {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    @Override
    public void close() {
        writer.close();
    }
}