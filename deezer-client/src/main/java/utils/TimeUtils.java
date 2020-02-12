package utils;

import java.time.LocalTime;
import java.util.ResourceBundle;

public final class TimeUtils {
    public static String secondsToNormalTime(int seconds, ResourceBundle resources) {
        LocalTime time = LocalTime.ofSecondOfDay(seconds);
        return (time.getHour() > 0 ? String.format("%d %s", time.getHour(), resources.getString("hours")) : "") +
                (time.getMinute() > 0 ? String.format("%d %s", time.getMinute(), resources.getString("minutes")) : "") +
                (time.getSecond() > 0 ? String.format("%d %s", time.getSecond(), resources.getString("seconds")) : "");
    }
}
