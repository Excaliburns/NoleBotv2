package util;

import apiconnect.ApiWebSocketConnector;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;

import java.time.Duration;
import java.util.List;

public class NoleBotUtil {
    @Getter @Setter
    private static JDA jda;

    @Getter @Setter
    private static ApiWebSocketConnector apiWebSocketConnector;

    public static String getFormattedDurationString(Duration duration) {
        String durationString = null;

        if (duration.toDaysPart() > 0) {
            durationString = String.format("%s Days, %s Hours, %s Minutes, %s Seconds",
                    duration.toDaysPart(),
                    duration.toHoursPart(),
                    duration.toMinutesPart(),
                    duration.toSecondsPart());
        }
        else if (duration.toHoursPart() > 0) {
            durationString = String.format("%s Hours, %s Minutes, %s Seconds",
                    duration.toHoursPart(),
                    duration.toMinutesPart(),
                    duration.toSecondsPart());
        }
        else if (duration.toMinutesPart() > 0) {
            durationString = String.format("%s Minutes, %s Seconds",
                    duration.toMinutesPart(),
                    duration.toSecondsPart());
        }
        else if (duration.toSecondsPart() > 0) {
            durationString = String.format("%s Seconds",
                    duration.toSecondsPart());
        }

        return durationString;
    }

    public static String getFormattedStringFromList(List<?> list) {
        return getFormattedStringFromListSeparatedByDelimiter(",", list);
    }

    public static String getFormattedStringFromListSeparatedByDelimiter(final String delimiter, List<?> list) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");

        for (Object o : list) {
            stringBuilder.append(o.toString());
            stringBuilder.append(",");
        }

        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        stringBuilder.append("]");

        return stringBuilder.toString();
    }
}
