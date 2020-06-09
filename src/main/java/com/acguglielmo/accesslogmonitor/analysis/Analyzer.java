package com.acguglielmo.accesslogmonitor.analysis;

import java.sql.SQLException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.BlockOccurrencesGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.threshold.Threshold;

@ApplicationScoped
public final class Analyzer {

	@Inject
    AccessLogGatewaySqlImpl accessLogGatewaySqlImpl;
    
	@Inject
    BlockOccurrencesGatewaySqlImpl blockOccurrencesGatewaySqlImpl; 

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
