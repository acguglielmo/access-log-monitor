package com.acguglielmo.accesslogmonitor.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.acguglielmo.accesslogmonitor.enums.Duration;

/**
 * The type Date utils.
 */
public final class DateUtils {
    /**
     * The constant DATE_FORMAT_FILE.
     */
    public static final DateTimeFormatter DATE_FORMAT_FILE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    /**
     * The constant DATE_FORMAT_ARGS.
     */
    public static final DateTimeFormatter DATE_FORMAT_ARGS = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");

    private volatile static DateUtils instance;

    private DateUtils(){}

    /**
     * Gets instance.
     *
     * @return the instance
     */
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

    /**
     * Gets start date.
     *
     * @param date the date
     * @return the start date
     */
    public LocalDateTime getStartDate(final String date) {
        return LocalDateTime.parse(date, DateUtils.DATE_FORMAT_ARGS);
    }

    /**
     * Gets end date.
     *
     * @param initialDate the initial date
     * @param duration    the duration
     * @return the end date
     */
    public LocalDateTime getEndDate(final LocalDateTime initialDate, final Duration duration) {
        switch (duration) {
            case HOURLY:  return initialDate.plusHours(1);
            case DAILY:   return initialDate.plusDays(1);
            default:      return LocalDateTime.now();
        }
    }
}
