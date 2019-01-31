package ru.vg.video.save;

import ru.vg.util.HelperPath;

import java.util.Calendar;

public class SaveImageForVideo extends AbstractSaveImage {
    public SaveImageForVideo(String camName) {
        super(camName);
    }

    protected String getImageOutDir() {
        return HelperPath.getOutDirPathImageForVideo(camName);
    }

    @Override
    protected String getFileName() {
        String nowTime = formatNowTimeFull.format(Calendar.getInstance().getTime());
        return String.format("%s__%s.%s", camName, nowTime, FORMAT_DEFAULT.toLowerCase());
    }
}
