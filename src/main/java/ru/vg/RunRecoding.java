package ru.vg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vg.util.HelperThread;
import ru.vg.video.recording.ThreadRecordingStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;
import static ru.vg.util.HelperProperties.getCamerasName;


public class RunRecoding {
    private static Logger log = LoggerFactory.getLogger(RunRecoding.class);

    public static void main(String[] args) throws Exception {
        Thread runForSleep = new Thread(() -> RunRecoding.runForSleep(Integer.valueOf(args[0]), args[1]));
        runForSleep.setName("run-for-sleep");
        runForSleep.start();

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

    private static void runForSleep(Integer hour, String cmd) {
        while (true) {
            LocalDateTime time = LocalDateTime.now();
            if (hour != time.getHour()) {
                log.info("Еще рано, ушел в сон");
                HelperThread.sleep(5 * 60 * 1_000);
            } else {
                try {
                    log.info("Ушел в сон");
                    Process p = Runtime.getRuntime().exec("cmd /c start \"\" " + cmd);
                    System.out.println("out " + getLines(p.getInputStream()));
                    System.err.println("out err " + getLines(p.getErrorStream()));
                    HelperThread.sleep(60 * 60 * 1_000);
                } catch (IOException ex) {
                    log.warn(ex.getMessage(), ex);
                }
            }
        }
    }

    private static void convertArchiveAndDeleted() {
        try {
            while (true) {
                if (LocalDateTime.now().getHour() == 0) {
                    ConvertImageInVideo.runConvert();
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

    private static String getLines(InputStream stream) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            while (reader.ready()) {
                sb.append(reader.readLine());
            }
            return sb.toString();
        } catch (IOException ex) {
            log.info("Не удалось прочить вывод ", ex);
        }
        return sb.toString();
    }
}