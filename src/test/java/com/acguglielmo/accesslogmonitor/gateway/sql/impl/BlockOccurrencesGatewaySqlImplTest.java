package com.acguglielmo.accesslogmonitor.gateway.sql.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.threshold.HourlyThreshold;
import com.acguglielmo.accesslogmonitor.threshold.Threshold;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(value = H2DatabaseTestResource.class)
public class BlockOccurrencesGatewaySqlImplTest {

	@Inject
	BlockOccurrencesGatewaySqlImpl instance;
	
	@BeforeEach
    public void beforeEach() throws Exception {

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

        instance.insert(list);
    }

}