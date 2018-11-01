package video.stream;

import com.xuggle.xuggler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ServiceUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static util.Time.Format.Image;
import static util.Time.Format.Video;
import static util.Time.currentTime;
import static util.Time.getStepShotForNightOrDay;

public class RecordingStream {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static ResourceBundle properties = ServiceUtil.getProperties();
    private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy_MM_dd");
    private static final String PATH_FOLDER_VIDEO = "/video";
    private static final String PATH_FOLDER_IMAGE = "/image";
    private static final String PATH_FOLDER_IMAGE_SHOT = "/imageShot";
    private static final long ONE_HOUR = 3_600;

    private final long recordingTime;
    private final String url;
    private final String videoOutDirFor;
    private final String imageOutDirFor;
    private final String imageShotOutDirFor;
    private final String camName;
    private final String camNameInProperties;
    private IContainer container;
    private int videoStreamId;
    private IStreamCoder videoCoder;
    private boolean isStopping;
    private List<String> controlTimeShot = createControlTimeShot();

    public RecordingStream(String camNameInProperties) {
        log.info(camNameInProperties + ": Loading properties");

        this.camNameInProperties = camNameInProperties;
        this.url = properties.getString("propt." + camNameInProperties + ".url");
        this.camName = properties.getString("propt." + camNameInProperties + ".name");
        double duration = Double.valueOf(properties.getString("propt." + camNameInProperties + ".recording.time.hour"));

        String currentDayFolder = "/" + YYYY_MM_DD.format(Calendar.getInstance().getTime());
        this.videoOutDirFor = properties.getString("path.save") + PATH_FOLDER_VIDEO + "/" + camName + currentDayFolder;
        this.imageOutDirFor = properties.getString("path.save") + PATH_FOLDER_IMAGE + currentDayFolder;
        this.imageShotOutDirFor = properties.getString("path.save") + PATH_FOLDER_IMAGE_SHOT + currentDayFolder;

        this.recordingTime = new Double(ONE_HOUR * duration).longValue();
    }

    private void init() {
        container = IContainer.make();

        if (container.open(url, IContainer.Type.READ, null) < 0)
            throw new IllegalArgumentException("could not open: " + url);
        int numStreams = container.getNumStreams();
        videoCoder = null;
        videoStreamId = -1;

        for (int i = 0; i < numStreams; i++) {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                Toolkit.getDefaultToolkit().getScreenSize();
                break;
            }
        }
        if (videoStreamId == -1)
            throw new RuntimeException(String.format("could not find video stream in container:\nurl = %s\ncamName = %s", url, camName));

        // пытаемся открыть кодек
        if (videoCoder.open() < 0)
            throw new RuntimeException(String.format("could not open video decoder for container:\nurl = %s\ncamName = %s", url, camName));
    }

    public void start() {
        init();
        IPacket packet = IPacket.make();
        long videoTimeShot = 0;
        long imageTimeShot = 0;
        SaveVideo video = new SaveVideo(videoOutDirFor, camName);
        SaveImage image = new SaveImage(imageOutDirFor, camName);
        SaveImage imageByTimeShot = new SaveImage(imageShotOutDirFor, camName);

        try {
            while (container.readNextPacket(packet) >= 0 && !isStopping && videoTimeShot < recordingTime) {
                if (packet.getStreamIndex() == videoStreamId) {
                    IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
                    int offset = 0;
                    while (offset < packet.getSize()) {
                        int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);

                        if (bytesDecoded < 0)
                            throw new RuntimeException(String.format("got error decoding video in:\nurl = %s\ncamName = %s", url, camName));
                        offset += bytesDecoded;

                        if (picture.isComplete()) {
                            IVideoPicture newPic = picture;
                            long timestamp = picture.getTimeStamp() / 1_000_000;
                            if (timestamp > videoTimeShot) {
                                BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                                video.writerImage(javaImage);
                                videoTimeShot += getStepShotForNightOrDay(Video, camNameInProperties);
                            }
                            if (timestamp > imageTimeShot) {
                                BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                                image.writerImage(javaImage);
                                imageTimeShot += getStepShotForNightOrDay(Image, camNameInProperties);
                            }
                            String nowTime = currentTime();
                            if (controlTimeShot.contains(nowTime)) {
                                BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                                imageByTimeShot.writerImage(javaImage);
                            }
                        }
                    }
                }
            }
            if (videoCoder != null) {
                videoCoder.close();
                videoCoder = null;
            }
            if (container != null) {
                container.close();
                container = null;
            }
        } finally {
            video.close();
        }
    }

    public void stopping() {
        isStopping = true;
    }

    private List<String> createControlTimeShot() {
        ResourceBundle properties = ServiceUtil.getProperties();
        List<String> timeShots = new LinkedList<>();
        for(String timeShot : properties.getString("control.time.image.shot").split(";")) {
            timeShots.add(timeShot);
        }
        return timeShots;
    }


}
