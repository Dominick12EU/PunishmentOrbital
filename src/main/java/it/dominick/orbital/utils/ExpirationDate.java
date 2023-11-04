package it.dominick.orbital.utils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

public class ExpirationDate {

    public static Timestamp calculateExpirationDate(String duration) {
        if (duration.equalsIgnoreCase("permanent") || !duration.matches("\\d+[smhdw]")) {
            return Timestamp.valueOf("9999-12-31 23:59:59");
        }

        String[] durationParts = duration.toLowerCase().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        int value = Integer.parseInt(durationParts[0]);
        String unit = durationParts[1];

        long durationMillis;

        switch (unit) {
            case "s":
                durationMillis = value * 1000;
                break;
            case "m":
                durationMillis = value * 1000 * 60;
                break;
            case "h":
                durationMillis = value * 1000 * 60 * 60;
                break;
            case "d":
                durationMillis = value * 1000 * 60 * 60 * 24;
                break;
            case "w":
                durationMillis = value * 1000 * 60 * 60 * 24 * 7;
                break;
            case "mo":
                durationMillis = value * 1000 * 60 * 60 * 24 * 30;
                break;
            default:
                return Timestamp.valueOf(LocalDateTime.MAX);
        }

        LocalDateTime expirationDateTime = LocalDateTime.now().plus(Duration.ofMillis(durationMillis));
        return Timestamp.valueOf(expirationDateTime);
    }

    public static String getTimeUntilNow(long timestampMillis) {
        Instant now = Instant.now();
        Instant expiration = Instant.ofEpochMilli(timestampMillis);

        Duration duration = Duration.between(now, expiration);
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return seconds + "s";
        } else {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;

            if (minutes < 60) {
                return minutes + "m " + remainingSeconds + "s";
            } else {
                long hours = minutes / 60;
                long remainingMinutes = minutes % 60;

                if (hours > 0) {
                    return hours + "h " + remainingMinutes + "m";
                } else {
                    return remainingMinutes + "m";
                }
            }
        }
    }
}
