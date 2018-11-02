package video.recording;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ServiceUtil;
import video.stream.RecordingStream;

import java.util.ResourceBundle;

public class ThreadRecordingStream extends Thread {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final long ONE_HOUR = 3_600_000;

    private static ResourceBundle properties = ServiceUtil.getProperties();

    private final long duration;
    private final long backup;
    private final String camNameInProperties;
    private RecordingStream recordingStream;

    public ThreadRecordingStream(String camNameInProperties) {
        super.setName("Main class nameCam = " + camNameInProperties);
        this.camNameInProperties = camNameInProperties;
        this.duration = new Double(ONE_HOUR * Double.valueOf(properties.getString("propt." + camNameInProperties + ".recording.time.hour"))).longValue();
        this.backup = new Double(ONE_HOUR * Double.valueOf(properties.getString("backup.time.hour"))).longValue();
    }

    private void startRecording() {
        long recording = 0;
        log.info(getName() + ": Creating recording file");
        recordingStream = new RecordingStream(camNameInProperties);

        log.info(getName() + ": Stating recording");
        recordingStream.start();

        while (recording < duration) {
            try {
                Thread.sleep(backup);
                recording += backup;
            } catch (InterruptedException ex) {
                log.warn("camNameInProperties : camNameInProperties", ex);
            }
            recordingStream.reInitVideo();
        }
        log.info(getName() + ": Finished recording");
        recordingStream.stopping();
    }

    @Override
    public void run() {
        startRecording();
    }

    public void stopping() {
        recordingStream.stopping();
    }
}
