package video.save;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;

public interface ISave {
    public static SimpleDateFormat formatNowTime = new SimpleDateFormat("HH_mm");

    public void writerImage(BufferedImage bgrScreen);

    public void close();
}
