package ru.vg;

import ru.vg.util.HelperThread;
import ru.vg.video.recording.ThreadRecordingStream;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;
import static ru.vg.util.HelperProperties.getCamerasName;


public class RunRecoding {
    public static void main(String a[]) throws Exception {
        Thread threadConvert = new Thread(RunRecoding::convertArchiveAndDeleted);
        threadConvert.setName(ConvertImageInVideo.class.getName());
        threadConvert.start();

        List<ThreadRecordingStream> threadRecordingStreams = createThreadRecodingStreamList();
        startThread(threadRecordingStreams);
        while (true) {
            if (isAlive(threadRecordingStreams)) {
                sleep(60_000);
            } else {
                stopping(threadRecordingStreams);
                threadConvert.stop();
                break;
            }
        }
    }

    private static void convertArchiveAndDeleted() {
        try {
            while (true) {
                if (LocalDateTime.now().getHour() == 0) {
                    ConvertImageInVideo.convertEveryDay();
                    ZipDir.runZip();
                    DeleteImageWithCheckZip.runDeleteImageWithCheckZip();
                }
                HelperThread.sleep(3600_000);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private static List<ThreadRecordingStream> createThreadRecodingStreamList() {
        List<ThreadRecordingStream> threadRecordingStreams = new LinkedList<>();
        for (String camName : getCamerasName()) {
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