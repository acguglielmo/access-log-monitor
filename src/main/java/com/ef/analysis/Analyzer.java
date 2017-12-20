package com.ef.analysis;

import com.ef.dto.BlockOccurrencesDto;
import com.ef.enums.Duration;
import com.ef.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.ef.gateway.sql.impl.BlockOccurrencesGatewaySqlImpl;
import com.ef.util.DateUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The type Analyzer.
 */
public final class Analyzer {

    private static volatile Analyzer instance;

    private Analyzer(){}

    /**
     * Gets instance.
     *
     * @return the instance
     */
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


    /**
     * Block by thresold list.
     *
     * @param startDate the start date
     * @param duration  the duration
     * @param threshold the threshold
     * @return the list
     */
    public List<BlockOccurrencesDto> blockByThresold(final String startDate,
                                                     final Duration duration,
                                                     final Integer threshold) {

        final DateUtils dateUtil = DateUtils.getInstance();

        final LocalDateTime start = dateUtil.getStartDate(startDate);
        final LocalDateTime end = dateUtil.getEndDate(start, duration);

        List<BlockOccurrencesDto> blockOccurrencesDtoList;
        try {
            blockOccurrencesDtoList = new AccessLogGatewaySqlImpl().find(start, end, threshold);
            new BlockOccurrencesGatewaySqlImpl().insert(blockOccurrencesDtoList);
            return blockOccurrencesDtoList;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
