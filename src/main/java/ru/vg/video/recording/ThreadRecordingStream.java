package ru.vg.video.recording;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vg.util.HelperProperties;
import ru.vg.util.HelperThread;
import ru.vg.video.stream.RecordingStream;

import java.util.ResourceBundle;

public class ThreadRecordingStream extends Thread {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final long ONE_HOUR = 3_600_000;

    private static ResourceBundle properties = HelperProperties.getProperties();

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
        while (true) {
            try {
                log.info(getName() + ": Creating recording file");
                recordingStream = new RecordingStream(camNameInProperties);

                log.info(getName() + ": Stating recording");
                recordingStream.start();
                break;
            } catch (Throwable ex) {
                log.error("Restart recording", ex);
            }
        }
    }

    @Override
    public void run() {
        long recording = 0;
        while (recording < duration) {
            startRecording();
            HelperThread.sleep(backup);
            recording += backup;
            log.info(getName() + ": Finished recording");
            recordingStream.stopping();
        }
    }

    public void stopping() {
        recordingStream.stopping();
    }
}
