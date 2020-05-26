package com.acguglielmo.accesslogmonitor.util;

import java.time.format.DateTimeFormatter;

public final class DateUtils {

    public static final DateTimeFormatter DATE_FORMAT_FILE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static final DateTimeFormatter DATE_FORMAT_ARGS = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");

    private DateUtils(){}

}
