package com.tf.sc.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parse(String dateStr) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static String nowStr() {
        return format(LocalDateTime.now());
    }

    public static boolean isBefore(String time1, String time2) {
        return parse(time1).isBefore(parse(time2));
    }

    public static boolean isAfter(String time1, String time2) {
        return parse(time1).isAfter(parse(time2));
    }

    public static boolean isBeforeNow(String time) {
        return parse(time).isBefore(LocalDateTime.now());
    }

    public static boolean isAfterNow(String time) {
        return parse(time).isAfter(LocalDateTime.now());
    }
}