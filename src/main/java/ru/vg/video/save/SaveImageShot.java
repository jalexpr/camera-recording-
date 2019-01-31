package ru.vg.video.save;

import ru.vg.util.HelperPath;

public class SaveImageShot extends AbstractSaveImage {
    public SaveImageShot(String camName) {
        super(camName);
    }

    protected String getImageOutDir() {
        return HelperPath.getOutDirPathImageShot(camName);
    }
}
