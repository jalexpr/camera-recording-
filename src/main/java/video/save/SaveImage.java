package video.save;

import util.HelperPath;

public class SaveImage extends AbstractSaveImage {
    public SaveImage(String camName) {
        super(camName);
    }

    protected String getImageOutDir() {
        return HelperPath.getOutDirPathImage(camName);
    }
}
