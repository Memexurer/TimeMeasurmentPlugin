package pl.tryhardujemy.playertime;

import java.util.concurrent.TimeUnit;

public final class TimeParsingUtils {
    public static String formatSecs(long secs) {
        long days = TimeUnit.SECONDS.toDays(secs);
        long hours = TimeUnit.SECONDS.toHours(secs) - TimeUnit.DAYS.toHours(days);
        long minutes = TimeUnit.SECONDS.toMinutes(secs) - TimeUnit.HOURS.toMinutes(hours) - TimeUnit.DAYS.toMinutes(days);
        long seconds = secs - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days);

        StringBuilder builder = new StringBuilder();
        if(days == 1) {
            builder.append("1 dzień");
        } else if(days > 1) {
            builder.append(days).append(" dni");
        }

        if(days != 0) builder.append(", ");

        if(hours == 1) {
            builder.append("1 godzina");
        } else if(hours > 1) {
            builder.append(hours).append(" godzin");
        }

        if(hours != 0) builder.append(", ");

        if(minutes == 1) {
            builder.append("1 minuta");
        } else if(minutes > 1) {
            builder.append(minutes).append(" minut");
        }

        if(minutes != 0) builder.append(" i ");

        if(seconds == 1) {
            builder.append("1 sekunda");
        } else if(seconds > 1) {
            builder.append(seconds).append(" sekund");
        }

        return builder.toString();
    }
}
