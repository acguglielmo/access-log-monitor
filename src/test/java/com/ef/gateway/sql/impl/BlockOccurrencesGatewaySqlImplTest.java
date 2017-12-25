package com.ef.gateway.sql.impl;

import com.ef.dto.BlockOccurrencesDto;
import com.ef.enums.Duration;
import com.ef.gateway.sql.ConnectionFactory;
import com.ef.util.DateUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;
import static org.powermock.api.support.SuppressCode.suppressConstructor;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {BlockOccurrencesGatewaySqlImpl.class, ConnectionFactory.class})
public class BlockOccurrencesGatewaySqlImplTest extends AbstractGatewaySqlImplTest {

    @BeforeClass
    public static void oneTime() throws SQLException, ClassNotFoundException {
        new BlockOccurrencesGatewaySqlImplTest().initDatabase();
    }

    @Override
    protected void initDatabase() throws SQLException, ClassNotFoundException {
        final Connection connection = super.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("SET DATABASE SQL SYNTAX MYS TRUE");
        statement.execute("CREATE SCHEMA usr_aguglielmo");
        statement.execute("CREATE TABLE usr_aguglielmo.block_occurrences (\n" +
                "  ip varchar(15) NOT NULL,\n" +
                "  start_date datetime(3) NOT NULL,\n" +
                "  end_date datetime(3) NOT NULL,\n" +
                "  threshold int(11) NOT NULL,\n" +
                "  comment varchar(200) NOT NULL,\n" +
                "  PRIMARY KEY (ip)\n" +
                ");");
        connection.commit();
    }

    @Test
    public void tableExistsTest() throws Exception {
        configureMock();
        new BlockOccurrencesGatewaySqlImpl().tableExists();
    }

    @Test
    public void insertTest() throws Exception {
        final LocalDateTime startDate = DateUtils.getInstance().getStartDate("2017-01-01.13:00:00");
        final LocalDateTime endDate = DateUtils.getInstance().getEndDate(startDate, Duration.HOURLY);

        final List<BlockOccurrencesDto> list = new ArrayList<>();
        final BlockOccurrencesDto dto1 = new BlockOccurrencesDto();
        dto1.setIp("192.168.90.4");
        dto1.setThreshold(100);
        dto1.setCount(34);
        dto1.setStartDate(startDate);
        dto1.setEndDate(endDate);
        list.add(dto1);

        for (int i = 1; i < 1200; i++) {
            final BlockOccurrencesDto dto = new BlockOccurrencesDto();
            dto.setIp("192.168.90.8");
            dto.setThreshold(100);
            dto.setCount(34);
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            list.add(dto);
        }

        configureMock();
        new BlockOccurrencesGatewaySqlImpl().insert(list);
    }

    private void configureMock() throws Exception {
        suppressConstructor(ConnectionFactory.class);
        mockStatic(ConnectionFactory.class);

        final ConnectionFactory mockedConnectionFactory = createMock(ConnectionFactory.class);
        expectNew(ConnectionFactory.class).andReturn(mockedConnectionFactory);
        expect(ConnectionFactory.getInstance()).andReturn(mockedConnectionFactory);
        expect(mockedConnectionFactory.getConnection()).andReturn(super.getConnection()).anyTimes();
        replay(mockedConnectionFactory);
        replay(ConnectionFactory.class);
    }
}