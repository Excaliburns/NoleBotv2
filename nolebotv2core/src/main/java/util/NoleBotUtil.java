package util;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;

import java.time.Duration;

public class NoleBotUtil {
    @Getter @Setter
    private static JDA jda;

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
}
