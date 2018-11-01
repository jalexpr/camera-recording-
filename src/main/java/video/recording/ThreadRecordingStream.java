package video.recording;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import video.stream.RecordingStream;

public class ThreadRecordingStream extends Thread {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String camNameInProperties;
    private RecordingStream recordingStream;

    public ThreadRecordingStream(String camNameInProperties) {
        this.camNameInProperties = camNameInProperties;
        this.setName(camNameInProperties);
    }

    private void startRecording() {
        log.info(getName() + ": Creating recording file");
        recordingStream = new RecordingStream(camNameInProperties);

        log.info(getName() + ": Stating recording");
        recordingStream.start();

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
