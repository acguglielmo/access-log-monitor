package com.acguglielmo.accesslogmonitor.gateway.sql.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.acguglielmo.accesslogmonitor.AbstractComponentTest;
import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.util.Threshold;

public class BlockOccurrencesGatewaySqlImplTest extends AbstractComponentTest {

    @Test
    public void insertTest() throws Exception {
    	
    	final Threshold threshold = new Threshold("2017-01-01.13:00:00", "hourly", "34");
    	
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