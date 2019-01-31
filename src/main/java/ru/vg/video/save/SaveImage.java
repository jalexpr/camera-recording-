package ru.vg.video.save;

import ru.vg.util.HelperPath;

public class SaveImage extends AbstractSaveImage {
    public SaveImage(String camName) {
        super(camName);
    }

    protected String getImageOutDir() {
        return HelperPath.getOutDirPathImage(camName);
    }
}
