package com.iemr.flw.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatToTimestamp(LocalDateTime dateTime) {
        try {
            if (dateTime != null) {
                return dateTime.format(OUTPUT_FORMATTER);
            }
        } catch (Exception e) {
            System.err.println("Error formatting date: " + e.getMessage());
        }
        return null;
    }
}
