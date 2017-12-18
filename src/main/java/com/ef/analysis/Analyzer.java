package com.ef.analysis;

import com.ef.dto.ThresholdDto;
import com.ef.enums.Duration;
import com.ef.gateway.ThresholdGateway;
import com.ef.gateway.sql.impl.ThresholdGatewaySqlImpl;
import com.ef.util.DateFormatter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Analyzer {

    public void analyze(final String startDate,
                        final String duration,
                        final String threshold) {

        final ThresholdGateway gateway = new ThresholdGatewaySqlImpl();

        final Date start = getInitialDate(startDate);
        final Date end = getFinalDate(start, Duration.getByName(duration));

        final List<ThresholdDto> thresholdDtos = gateway.find(start, end, threshold);

        thresholdDtos.forEach(System.out::println);
        thresholdDtos.forEach(gateway::insert);

        gateway.close();
    }

    private Date getInitialDate(final String date) {
        if (date != null) {
            try {
                return DateFormatter.DATE_FORMAT_ARGS.parse(date);
            } catch (final ParseException e) {
                e.printStackTrace();
            }
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        return calendar.getTime();
    }

    private Date getFinalDate(final Date initialDate, final Duration duration) {

        if (duration == null) {
            return null;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(initialDate);

        switch (duration) {
            case HOURLY: calendar.add(Calendar.HOUR, 1);
                break;
            case DAILY: calendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
        }
        return calendar.getTime();
    }
}
