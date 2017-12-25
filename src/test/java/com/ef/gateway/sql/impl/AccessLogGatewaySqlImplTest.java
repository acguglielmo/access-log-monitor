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


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.powermock.api.easymock.PowerMock.*;
import static org.powermock.api.support.SuppressCode.suppressConstructor;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {AccessLogGatewaySqlImpl.class, ConnectionFactory.class})
public class AccessLogGatewaySqlImplTest extends AbstractGatewaySqlImplTest {

    @BeforeClass
    public static void oneTime() throws SQLException, ClassNotFoundException {
        new AccessLogGatewaySqlImplTest().initDatabase();
    }

    @Override
    protected void initDatabase() throws SQLException, ClassNotFoundException {
            Connection connection = super.getConnection();
            final Statement statement = connection.createStatement();
            statement.execute("SET DATABASE SQL SYNTAX MYS TRUE");
            statement.execute("CREATE SCHEMA usr_aguglielmo");
            statement.execute("CREATE TABLE usr_aguglielmo.access_log (\n" +
                    "  date datetime(3) NOT NULL,\n" +
                    "  ip varchar(15) NOT NULL,\n" +
                    "  request varchar(45) NOT NULL,\n" +
                    "  status int(11) NOT NULL,\n" +
                    "  user_agent varchar(200) NOT NULL,\n" +
                    "  PRIMARY KEY (date, ip),\n" +
                    ");\n");

            statement.executeUpdate("INSERT INTO usr_aguglielmo.access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:11.763','192.168.234.82','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
            statement.executeUpdate("INSERT INTO usr_aguglielmo.access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:21.164','192.168.234.82','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
            statement.executeUpdate("INSERT INTO usr_aguglielmo.access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:23.003','192.168.169.194','\"GET / HTTP/1.1\"',200,'\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.79 Safari/537.36 Edge/14.14393\"');");
            statement.executeUpdate("INSERT INTO usr_aguglielmo.access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:40.554','192.168.234.82','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
            statement.executeUpdate("INSERT INTO usr_aguglielmo.access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:54.583','192.168.169.194','\"GET / HTTP/1.1\"',200,'\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.79 Safari/537.36 Edge/14.14393\"');");
            connection.commit();
    }

    @Test
    public void tableExistsTest() throws Exception {
        configureMock();
        new AccessLogGatewaySqlImpl().tableExists();
    }

    @Test
    public void insertTest() throws Exception {
        configureMock();
        final Pattern quote = Pattern.compile(Pattern.quote("|"));
        final List<String[]> list = new ArrayList<>();
        list.add(quote.split("2017-01-01 23:59:56.907|192.168.167.234|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0"));
        list.add(quote.split("2017-01-01 23:59:57.335|192.168.25.244|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36\""));
        list.add(quote.split("2017-01-01 23:59:58.735|192.168.114.222|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:55.0) Gecko/20100101 Firefox/55.0\""));
        list.add(quote.split("2017-01-01 23:59:58.777|192.168.229.251|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36\""));
        list.add(quote.split("2017-01-01 23:59:58.791|192.168.229.98|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36\""));
        new AccessLogGatewaySqlImpl().insert(list);
    }

    @Test
    public void findTest() throws Exception {
        configureMock();
        final LocalDateTime startDate = DateUtils.getInstance().getStartDate("2017-01-01.00:00:11");
        final LocalDateTime endDate = DateUtils.getInstance().getEndDate(startDate, Duration.HOURLY);

        final List<BlockOccurrencesDto> blockOccurrencesDtos = new AccessLogGatewaySqlImpl().find(startDate, endDate, 1);
        assertNotNull(blockOccurrencesDtos);
        assertEquals(2, blockOccurrencesDtos.size());
        assertEquals("192.168.234.82", blockOccurrencesDtos.get(0).getIp());
        assertEquals(new Integer(3), blockOccurrencesDtos.get(0).getCount());
        assertEquals("192.168.169.194", blockOccurrencesDtos.get(1).getIp());
        assertEquals(new Integer(2), blockOccurrencesDtos.get(1).getCount());
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