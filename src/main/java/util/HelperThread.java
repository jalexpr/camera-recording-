package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelperThread {
    private static Logger log = LoggerFactory.getLogger(HelperThread.class);

    public static void sleepDefaultTime() {
        sleep(Thread.currentThread(), 10_000);
    }

    public static void sleep(Thread thread, long msTime) {
        try {
            log.warn("I am sleep to {} ms", msTime);
            thread.sleep(msTime);
        } catch (InterruptedException e) {
            log.warn("I am not sleep!");
        }
    }
}
