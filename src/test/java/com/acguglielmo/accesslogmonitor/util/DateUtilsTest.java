package com.acguglielmo.accesslogmonitor.util;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;

public class DateUtilsTest {

    private static final String START_DATE = "2017-12-18.14:01:09";

    @Test
    public void getStartDateTest() throws Exception {
        final Threshold threshold = new Threshold(START_DATE, null, "1");
    	
    	final LocalDateTime startDate = threshold.getStartDate();

        assertEquals(2017, startDate.getYear());
        assertEquals(12, startDate.getMonth().getValue());
        assertEquals(18, startDate.getDayOfMonth());
        assertEquals(14, startDate.getHour());
        assertEquals(1, startDate.getMinute());
        assertEquals(9, startDate.getSecond());
    }

    @Test
    public void getEndDateTest() throws Exception {
        final Threshold dailyThreshold = new Threshold(START_DATE, "daily", "1");
    	
        final LocalDateTime endDateDailyTest = dailyThreshold.getEndDate();
        assertEquals(2017, endDateDailyTest.getYear());
        assertEquals(12, endDateDailyTest.getMonth().getValue());
        assertEquals(19, endDateDailyTest.getDayOfMonth());
        assertEquals(14, endDateDailyTest.getHour());
        assertEquals(1, endDateDailyTest.getMinute());
        assertEquals(9, endDateDailyTest.getSecond());

        final Threshold hourlyThreshold = new Threshold(START_DATE, "hourly", "1");
    	
        final LocalDateTime endDateHourlyTest = hourlyThreshold.getEndDate();
        assertEquals(2017, endDateHourlyTest.getYear());
        assertEquals(12, endDateHourlyTest.getMonth().getValue());
        assertEquals(18, endDateHourlyTest.getDayOfMonth());
        assertEquals(15, endDateHourlyTest.getHour());
        assertEquals(1, endDateHourlyTest.getMinute());
        assertEquals(9, endDateHourlyTest.getSecond());
    }

}