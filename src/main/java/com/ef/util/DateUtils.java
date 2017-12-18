package com.ef.util;

import com.ef.enums.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    public static final DateTimeFormatter DATE_FORMAT_FILE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final DateTimeFormatter DATE_FORMAT_ARGS = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");

    private static DateUtils instance;

    private DateUtils(){}

    public static DateUtils getInstance() {
        if (instance == null) {
            synchronized (DateUtils.class) {
                if (instance == null) {
                    instance = new DateUtils();
                }
            }
        }
        return instance;
    }

    public LocalDateTime getStartDate(final String date) {
        return LocalDateTime.parse(date, DateUtils.DATE_FORMAT_ARGS);
    }

    public LocalDateTime getEndDate(final LocalDateTime initialDate, final Duration duration) {
        switch (duration) {
            case HOURLY:  return initialDate.plusHours(1);
            case DAILY:   return initialDate.plusDays(1);
            default:      return LocalDateTime.now();
        }
    }
}
