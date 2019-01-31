package ru.vg.video.save;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;

public interface ISave {
    public static SimpleDateFormat formatNowTime = new SimpleDateFormat("HH_mm");
    public static SimpleDateFormat formatNowTimeFull = new SimpleDateFormat("HH_mm_ss_SS");

    public void writerImage(BufferedImage bgrScreen);

    public void close();
}
