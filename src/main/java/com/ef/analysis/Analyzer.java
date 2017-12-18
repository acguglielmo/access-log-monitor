package com.ef.analysis;

import com.ef.dto.BlockOccurrencesDto;
import com.ef.enums.Duration;
import com.ef.gateway.ThresholdGateway;
import com.ef.gateway.sql.impl.ThresholdGatewaySqlImpl;
import com.ef.util.DateUtils;

import java.time.LocalDateTime;
import java.util.List;

public final class Analyzer {

    private static Analyzer instance;

    private Analyzer(){}

    public static Analyzer getInstance() {
        if (instance == null) {
            synchronized (Analyzer.class) {
                if (instance == null) {
                    instance = new Analyzer();
                }
            }
        }
        return instance;
    }


    public void blockByThresold(final String startDate,
                                final String duration,
                                final Integer threshold) {

        final ThresholdGateway gateway = new ThresholdGatewaySqlImpl();

        final DateUtils dateUtil = DateUtils.getInstance();

        final LocalDateTime start = dateUtil.getStartDate(startDate);
        final LocalDateTime end = dateUtil.getEndDate(start, Duration.getByName(duration));

        final List<BlockOccurrencesDto> blockOccurrencesDtoList = gateway.find(start, end, threshold);

        blockOccurrencesDtoList.forEach(System.out::println);
        gateway.insert(blockOccurrencesDtoList);
    }
}
