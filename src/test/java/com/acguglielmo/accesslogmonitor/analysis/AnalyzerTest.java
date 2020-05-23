package com.acguglielmo.accesslogmonitor.analysis;

import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.enums.Duration;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.BlockOccurrencesGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.util.DateUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.*;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Analyzer.class, AccessLogGatewaySqlImpl.class, BlockOccurrencesGatewaySqlImpl.class})
public class AnalyzerTest {

    private Analyzer instance;

    private static final String startDateString = "2017-01-01.15:00:06";

    @Before
    public void setUp() throws Exception {
        this.instance = Analyzer.getInstance();
    }


    @Test
    public void getInstanceTest() throws Exception {
        assertEquals(instance, Analyzer.getInstance());
    }

    @Test
    public void blockByHourlyThresoldTest() throws Exception {

        final String startDateString = "2017-01-01.15:00:06";

        final LocalDateTime startDate = DateUtils.getInstance().getStartDate(startDateString);
        final LocalDateTime endDateHourly = DateUtils.getInstance().getEndDate(startDate, Duration.HOURLY);

        final List<BlockOccurrencesDto> list = new ArrayList<>();
        final BlockOccurrencesDto dto = getBlockOccurrencesDto(startDate, endDateHourly);
        list.add(dto);

        prepareExpectations(startDate, endDateHourly, list);

        final List<BlockOccurrencesDto> blockOccurrencesDtos =
                instance.blockByThresold(startDateString, Duration.HOURLY, 1);

        assertNotNull(blockOccurrencesDtos);
        assertFalse(blockOccurrencesDtos.isEmpty());
        assertEquals(dto, blockOccurrencesDtos.get(0));
    }

    private BlockOccurrencesDto getBlockOccurrencesDto(final LocalDateTime startDate,
                                                       final LocalDateTime endDate) {
        final BlockOccurrencesDto dto = new BlockOccurrencesDto();
        dto.setCount(1);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setIp("192.168.98.21");
        dto.setThreshold(1);
        return dto;
    }

    @Test
    public void blockByDailyThresoldTest() throws Exception {

        final LocalDateTime startDate = DateUtils.getInstance().getStartDate(startDateString);
        final LocalDateTime endDateDaily = DateUtils.getInstance().getEndDate(startDate, Duration.DAILY);

        final List<BlockOccurrencesDto> list = new ArrayList<>();
        final BlockOccurrencesDto dto = getBlockOccurrencesDto(startDate, endDateDaily);
        list.add(dto);

        prepareExpectations(startDate, endDateDaily, list);

        final List<BlockOccurrencesDto> blockOccurrencesDtos =
                instance.blockByThresold(startDateString, Duration.DAILY, 1);

        assertNotNull(blockOccurrencesDtos);
        assertFalse(blockOccurrencesDtos.isEmpty());
        assertEquals(dto, blockOccurrencesDtos.get(0));
    }

    private void prepareExpectations(final LocalDateTime startDate, final LocalDateTime endDate,
                                     final List<BlockOccurrencesDto> list) throws Exception {
        final AccessLogGatewaySqlImpl mockedGateway = createMock(AccessLogGatewaySqlImpl.class);
        expectNew(AccessLogGatewaySqlImpl.class).andReturn(mockedGateway);
        expect(mockedGateway.find(startDate, endDate, 1)).andReturn(list);
        replay(mockedGateway, AccessLogGatewaySqlImpl.class);

        final BlockOccurrencesGatewaySqlImpl mockedGatewayBlock = createMock(BlockOccurrencesGatewaySqlImpl.class);
        expectNew(BlockOccurrencesGatewaySqlImpl.class).andReturn(mockedGatewayBlock);
        mockedGatewayBlock.insert(list);
        expectLastCall().once();
        replay(mockedGatewayBlock, BlockOccurrencesGatewaySqlImpl.class);
    }

}