package com.ubn.hairsalon.config.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {

    public static String getElapsedTime(LocalDateTime createdDateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdDateTime, now);
        long seconds = duration.getSeconds();
        long minutes = seconds / 60;

        if (minutes < 1) {
            return "Written just now";
        } else if (minutes == 1) {
            return "Written by 1 min ago";
        } else if (minutes < 60) {
            return "Written by " + minutes + " mins ago";
        } else if (minutes < 1440) {
            long hours = minutes / 60;
            return "Written by " + hours + " hours ago";
        } else {
            long days = minutes / 1440;
            return "Written by " + days + " days ago";
        }
    }
}