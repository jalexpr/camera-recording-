package save.video.stream;

import com.xuggle.xuggler.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ReadImageFromSteam {
    public static final long ONE_HOUR = 1 * 3_600 * 1_000;
    private final String url;
    private final String smallFileName;
    private IContainer container;
    private int videoStreamId;
    private IStreamCoder videoCoder;
    private boolean isStopping;
    private int stepOneShot;

    public ReadImageFromSteam(String url, String smallFileName, int stepOneShot) {
        this.url = url;
        this.smallFileName = smallFileName;
        this.stepOneShot = stepOneShot * 1_000;
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
            throw new RuntimeException(String.format("could not find video stream in container:\nurl = %s\nsmallFileName = %s", url, smallFileName));

        // пытаемся открыть кодек
        if (videoCoder.open() < 0)
            throw new RuntimeException(String.format("could not open video decoder for container:\nurl = %s\nsmallFileName = %s", url, smallFileName));
    }

    public void start() throws IOException {
        init();
        IPacket packet = IPacket.make();
        int timeShot = 0;
        SaveVideo video = new SaveVideo(smallFileName);

        while (container.readNextPacket(packet) >= 0 && !isStopping && timeShot < ONE_HOUR) {
            if (packet.getStreamIndex() == videoStreamId) {
                IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
                int offset = 0;
                while (offset < packet.getSize()) {
                    int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);

                    if (bytesDecoded < 0)
                        throw new RuntimeException(String.format("got error decoding video in:\nurl = %s\nsmallFileName = %s", url, smallFileName));
                    offset += bytesDecoded;

                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;
                        long timestamp = picture.getTimeStamp();
                        if (timestamp > timeShot) {
                            // Получаем стандартный BufferedImage
                            BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                            writeInImge(timestamp, javaImage);
                            video.writerImage(javaImage);
                            timeShot += stepOneShot;
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
        video.close();
    }

    private void writeInImge(long timestamp, BufferedImage javaImage) throws IOException {
        String fileName = String.format("%07d.png", timestamp);
        ImageIO.write(javaImage, "PNG", new File("C:/temp/pictures/", fileName));
    }

    public void stopping() {
        isStopping = true;
    }
}
