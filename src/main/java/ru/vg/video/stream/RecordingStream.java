package ru.vg.video.stream;

import com.xuggle.xuggler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vg.util.HelperProperties;
import ru.vg.util.HelperThread;
import ru.vg.video.save.SaveImage;
import ru.vg.video.save.SaveImageForVideo;
import ru.vg.video.save.SaveImageShot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static ru.vg.util.HelperProperties.getCameraNameForDir;
import static ru.vg.util.HelperProperties.reReadProperties;
import static ru.vg.util.Time.Format.Image;
import static ru.vg.util.Time.Format.Video;
import static ru.vg.util.Time.currentTime;
import static ru.vg.util.Time.getStepShotForNightOrDay;

public class RecordingStream extends Thread {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static ResourceBundle properties = HelperProperties.getProperties();

    private String url;
    private final String camName;
    private final String camNameInProperties;
    private IContainer container;
    private int videoStreamId;
    private IStreamCoder videoCoder;
    private boolean isStopping;
    private List<String> controlTimeShot = createControlTimeShot();
    private boolean isSaveVideo;

    public RecordingStream(String camNameInProperties) {
        log.info(camNameInProperties + ": Loading properties");

        super.setName(camNameInProperties);
        this.camNameInProperties = camNameInProperties;
        this.url = properties.getString("propt." + camNameInProperties + ".url");
        this.camName = getCameraNameForDir(camNameInProperties);
        this.isSaveVideo = Boolean.valueOf(properties.getString("video." + camNameInProperties + ".save"));
    }

    private void reInitUrl() {
        container = IContainer.make();

        openUrlForContainer();

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

        log.info("ReInit");
    }

    private void openUrlForContainer() {
        boolean isOpenUrl = false;
        while (!isOpenUrl) {
            if (container.open(url, IContainer.Type.READ, null) >= 0) {
                log.info("open: " + url);
                isOpenUrl = true;
            } else {
                log.warn("could not open: " + url);
                HelperThread.sleepDefaultTime();
                reReadProperties();
                url = properties.getString("propt." + camNameInProperties + ".url");
            }
        }
    }

    @Override
    public void run() {
        if (isSaveVideo) {
            saveShotAndVideo();
        } else {
            saveShot();
        }
    }

    private void saveShotAndVideo() {
        reInitUrl();
        IPacket packet = IPacket.make();
        long videoTimeShot = 0;
        long imageTimeShot = 0;

        SaveImageForVideo imageVideo = new SaveImageForVideo(camName);
        SaveImage image = new SaveImage(camName);
        SaveImageShot imageByTimeShot = new SaveImageShot(camName);

        try {
            while (!isStopping) {
                if (container.readNextPacket(packet) >= 0) {
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
                                BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                                long timestamp = picture.getTimeStamp() / 1_000_000;
                                if (timestamp > imageTimeShot) {
                                    image.writerImage(javaImage);
                                    imageTimeShot += getStepShotForNightOrDay(Image, camNameInProperties);
                                }
                                if (timestamp > videoTimeShot && isSaveVideo) {
                                    imageVideo.writerImage(javaImage);
                                    videoTimeShot += getStepShotForNightOrDay(Video, camNameInProperties);
                                }
                            }
                        }
                    }
                } else {
                    log.warn("Not stream");
                    reInitUrl();
                }
            }
            log.info("Stop. Go out!");
            if (videoCoder != null) {
                videoCoder.close();
                videoCoder = null;
            }
            if (container != null) {
                container.close();
                container = null;
            }
        } catch (Throwable ex) {
            log.error(camName + " stoped!", ex);
        }
    }

    private void saveShot() {
        try {
            while (!isStopping) {
                reInitUrl();
                IPacket packet = IPacket.make();

                SaveImage image = new SaveImage(camName);
                SaveImageShot imageByTimeShot = new SaveImageShot(camName);

                boolean isSaved = false;
                while (!isSaved) {
                    if (container.readNextPacket(packet) >= 0) {
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
                                    BufferedImage javaImage = Utils.videoPictureToImage(newPic);

                                    image.writerImage(javaImage);
                                    isSaved = true;

                                    String nowTime = currentTime().substring(0, 3) + "00"; // костыль
                                    if (controlTimeShot.contains(nowTime)) {
                                        imageByTimeShot.writerImage(javaImage);
                                    }
                                }
                            }
                        }
                    } else {
                        log.warn("Not stream");
                        reInitUrl();
                    }
                }

                close();
                HelperThread.sleep(getStepShotForNightOrDay(Image, camNameInProperties) * 1_000);
            }
            close();
        } catch (Throwable ex) {
            log.error(camName + " stoped!", ex);
        }
    }

    public void stopping() {
        isStopping = true;
    }

    private List<String> createControlTimeShot() {
        ResourceBundle properties = HelperProperties.getProperties();
        List<String> timeShots = new LinkedList<>();
        for (String timeShot : properties.getString("control.time.image.shot").split(";")) {
            timeShots.add(timeShot);
        }
        return timeShots;
    }

    private void close() {
        log.info("Stop. Go out!");
        if (videoCoder != null) {
            videoCoder.close();
            videoCoder = null;
        }
        if (container != null) {
            container.close();
            container = null;
        }
    }
}
