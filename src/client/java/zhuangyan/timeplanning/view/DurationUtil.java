package zhuangyan.timeplanning.view;

import java.time.Duration;

public class DurationUtil {
    public static String formatDurationString(Duration duration) {
        int minutes = duration.toMinutesPart();
        int hours = duration.toHoursPart();
        int days = (int) duration.toDaysPart();
        String minutesString = minutes > 0 ? (minutes < 10 ? "0" : "") + minutes + "m" : "";
        String hoursString = hours > 0 ? (hours < 10 ? "0" : "") + hours + "h" : "";
        String daysString = days > 0 ? (days < 10 ? "0" : "") + days + "d" : "";
        String result = minutesString;
        if (!hoursString.isEmpty()) {
            result = hoursString + " " + result;
        }
        if (!daysString.isEmpty()) {
            result = daysString + " " + result;
        }
        return result;
    }
}
