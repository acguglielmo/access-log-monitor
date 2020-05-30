package com.acguglielmo.accesslogmonitor.gateway.sql.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.acguglielmo.accesslogmonitor.AbstractComponentTest;
import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.threshold.HourlyThreshold;
import com.acguglielmo.accesslogmonitor.threshold.Threshold;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

public class BlockOccurrencesGatewaySqlImplTest extends AbstractComponentTest {

	@Before
	public void before() {
		
		FixtureFactoryLoader.loadTemplates("com.acguglielmo.accesslogmonitor.template");
		
	}
	
    @Test
    public void insertTest() throws Exception {
    	
    	final Threshold threshold = Fixture.from(HourlyThreshold.class).gimme("2017-01-01.13:00:00, 34");
    	
        final List<BlockOccurrencesDto> list = new ArrayList<>();
        final BlockOccurrencesDto dto1 = BlockOccurrencesDto.builder()
        	.ip("192.168.90.4")
        	.threshold(100)
        	.count(34)
        	.startDate(threshold.getStartDate())
        	.endDate(threshold.getEndDate())
        	.build();
        list.add(dto1);

        for (int i = 1; i < 1200; i++) {
            final BlockOccurrencesDto dto = BlockOccurrencesDto.builder()
            	.ip("192.168.90.8")
            	.threshold(100)
            	.count(34)
            	.startDate(threshold.getStartDate())
            	.endDate(threshold.getEndDate())
            	.build();
            list.add(dto);
        }

        new BlockOccurrencesGatewaySqlImpl().insert(list);
    }

}