package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

public class Time {
    private static final ResourceBundle properties = HelperProperties.getProperties();
    private static final SimpleDateFormat HH_MM = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy_MM_dd");

    public static long getStepShotForNightOrDay(Format format, String camNameInProperties) {
        String partOfDay;
        if (isDay()) {
            partOfDay = "day";
        } else {
            partOfDay = "night";
        }

        return Integer.valueOf(properties.getString(String.format("%s%s.step.one.shot.%s.second", format.name, camNameInProperties, partOfDay)));
    }

    private static boolean isDay() {
        String dayTime = properties.getString("day");
        String nightTime = properties.getString("night");
        String currentTime = currentTime();

        return currentTime.compareTo(dayTime) > 0 && currentTime.compareTo(nightTime) < 0;
    }

    public static String currentTime() {
        return HH_MM.format(Calendar.getInstance().getTime());
    }

    public static String currentDay() {
        return YYYY_MM_DD.format(Calendar.getInstance().getTime());
    }

    public enum Format {
        Video("video."),
        Image("image.");

        public final String name;

        Format(String name) {
            this.name = name;
        }
    }
}
