import util.ServiceUtil;
import video.recording.ThreadRecordingStream;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class UtilSaveVideo {
    private static ResourceBundle properties = ServiceUtil.getProperties();

    public static void main(String a[]) throws Exception {
        List<ThreadRecordingStream> threadRecordingStreams = createThreadRecodingStreamList();
        startThread(threadRecordingStreams);
        while (true) {
            if (isAlive(threadRecordingStreams)) {
                Thread.sleep(30_000);
            } else {
                stopping(threadRecordingStreams);
                break;
            }
        }
    }

    private static List<ThreadRecordingStream> createThreadRecodingStreamList() {
        List<ThreadRecordingStream> threadRecordingStreams = new LinkedList<>();
        for (String camName : properties.getString("came.name").split(";")) {
            threadRecordingStreams.add(new ThreadRecordingStream(camName));
        }
        return threadRecordingStreams;
    }

    private static void startThread(List<ThreadRecordingStream> threadRecordingStreams) {
        for (ThreadRecordingStream threadRecordingStream : threadRecordingStreams) {
            threadRecordingStream.start();
        }
    }

    private static boolean isAlive(List<ThreadRecordingStream> threadRecordingStreams) {
        for (ThreadRecordingStream thread : threadRecordingStreams) {
            if (thread.isAlive()) {
                return true;
            }
        }
        return false;
    }

    private static void stopping(List<ThreadRecordingStream> threadRecordingStreams) {
        for (ThreadRecordingStream thread : threadRecordingStreams) {
            thread.stopping();
        }
    }
}