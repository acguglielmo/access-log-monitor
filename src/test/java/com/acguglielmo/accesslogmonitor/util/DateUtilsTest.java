package com.acguglielmo.accesslogmonitor.util;

import com.acguglielmo.accesslogmonitor.enums.Duration;
import com.acguglielmo.accesslogmonitor.util.DateUtils;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;

import static org.junit.Assert.*;

public class DateUtilsTest {

    private DateUtils instance;
    private String startDate;

    @Before
    public void setUp() throws Exception {
        this.instance = DateUtils.getInstance();

        final TemporalAccessor temporalAccessor = DateUtils.DATE_FORMAT_ARGS.parse("2017-12-18.14:01:09");
        final String temporalAcessorString = temporalAccessor.toString();
        startDate = temporalAcessorString.substring(temporalAcessorString.indexOf("2")).replace('T', '.');

    }

    @Test
    public void getInstanceTest() throws Exception {
        assertEquals(instance, DateUtils.getInstance());
    }

    @Test
    public void getStartDateTest() throws Exception {
        final LocalDateTime startDate = instance.getStartDate(this.startDate);

        assertEquals(2017, startDate.getYear());
        assertEquals(12, startDate.getMonth().getValue());
        assertEquals(18, startDate.getDayOfMonth());
        assertEquals(14, startDate.getHour());
        assertEquals(1, startDate.getMinute());
        assertEquals(9, startDate.getSecond());
    }

    @Test
    public void getEndDateTest() throws Exception {
        final LocalDateTime startDate = instance.getStartDate(this.startDate);

        final LocalDateTime endDateDailyTest = instance.getEndDate(startDate, Duration.DAILY);
        assertEquals(2017, endDateDailyTest.getYear());
        assertEquals(12, endDateDailyTest.getMonth().getValue());
        assertEquals(19, endDateDailyTest.getDayOfMonth());
        assertEquals(14, endDateDailyTest.getHour());
        assertEquals(1, endDateDailyTest.getMinute());
        assertEquals(9, endDateDailyTest.getSecond());

        final LocalDateTime endDateHourlyTest = instance.getEndDate(startDate, Duration.HOURLY);
        assertEquals(2017, endDateHourlyTest.getYear());
        assertEquals(12, endDateHourlyTest.getMonth().getValue());
        assertEquals(18, endDateHourlyTest.getDayOfMonth());
        assertEquals(15, endDateHourlyTest.getHour());
        assertEquals(1, endDateHourlyTest.getMinute());
        assertEquals(9, endDateHourlyTest.getSecond());
    }

}