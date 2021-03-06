package ru.vg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
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
import static ru.vg.util.HelperProperties.*;

@Service
public class RunRecoding implements InitializingBean {
    private static Logger log = LoggerFactory.getLogger(RunRecoding.class);

    @Override
    public void afterPropertiesSet() {
        RunRecoding.main(new String[]{});
    }

    public static void main(String[] args) {
        Thread runForSleep = new Thread(() -> {
            Thread threadConvert = new Thread(RunRecoding::convertArchiveAndDeleted);
            threadConvert.setName(ConvertImageInVideo.class.getName());
            threadConvert.start();

            List<ThreadRecordingStream> threadRecordingStreams = createThreadRecodingStreamList();
            startThread(threadRecordingStreams);
            while (true) {
                if (isAlive(threadRecordingStreams)) {
                    try {
                        sleep(60_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    stopping(threadRecordingStreams);
                    threadConvert.stop();
                    break;
                }
            }
        });
        runForSleep.setName("run-recoding");
        runForSleep.start();
    }

    private static void runForSleep(Integer hour, String cmd) {
        while (true) {
            LocalDateTime time = LocalDateTime.now();
            if (hour != time.getHour()) {
                log.info("Еще рано, поток ушел в сон");
                HelperThread.sleep(5 * 60 * 1_000);
            } else {
                try {
//                    ConvertImageInVideo.runConvert();
                    log.info("Попытка ПК уйти в сон");
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
            while (runAutoConvertAndArchive()) {
                if (LocalDateTime.now().getHour() == runAutoConvertAndArchiveHour()) {
                    ConvertImageInVideo.runConvert();
                    ZipDir.runZip();
                    DeleteImageWithCheckZip.runDeleteImageWithCheckZip();
                    HelperThread.sleep(23 * 3600_000 + 25 * 60_000);
                }
                HelperThread.sleep(60_000);
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