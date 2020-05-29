package com.acguglielmo.accesslogmonitor.analysis;

import java.sql.SQLException;
import java.util.List;

import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.BlockOccurrencesGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.util.Threshold;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class Analyzer {

    private final AccessLogGatewaySqlImpl accessLogGatewaySqlImpl;
    
    private final BlockOccurrencesGatewaySqlImpl blockOccurrencesGatewaySqlImpl; 

	public List<BlockOccurrencesDto> blockByThresold(final Threshold threshold) {

        try {
			final List<BlockOccurrencesDto> blockOccurrencesDtoList = accessLogGatewaySqlImpl.find(threshold);
			blockOccurrencesGatewaySqlImpl.insert(blockOccurrencesDtoList);
            return blockOccurrencesDtoList;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
