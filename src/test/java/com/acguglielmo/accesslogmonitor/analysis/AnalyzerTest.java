package com.acguglielmo.accesslogmonitor.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.acguglielmo.accesslogmonitor.AbstractComponentTest;
import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.util.Threshold;

public class AnalyzerTest extends AbstractComponentTest {

    private Analyzer instance;

    private static final String startDateString = "2017-01-01.00:00:00";

    @Before
    public void setUp() throws Exception {
        this.instance = Analyzer.getInstance();
    }
    
    @After
    public void cleanUp() throws Exception {
    	
		final Connection connection = getConnection();
		final Statement statement = connection.createStatement();
    	
		statement.executeUpdate("DELETE FROM access_log;");
		connection.commit();
		
    }


    @Test
    public void getInstanceTest() throws Exception {
        assertEquals(instance, Analyzer.getInstance());
    }

    @Test
    public void blockByHourlyThresoldTest() throws Exception {

		final Connection connection = getConnection();
		final Statement statement = connection.createStatement();
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:11.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:01:11.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:02:11.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:03:11.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:04:11.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:05:00.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		connection.commit();

		final Threshold threshold = new Threshold(startDateString, "hourly", "1");
		
        final List<BlockOccurrencesDto> blockOccurrencesDtos =
        	instance.blockByThresold(threshold);

        assertNotNull(blockOccurrencesDtos);
        assertFalse(blockOccurrencesDtos.isEmpty());
        assertEquals(1, blockOccurrencesDtos.size());
        assertEquals("192.168.98.20", blockOccurrencesDtos.get(0).getIp());
    }


    @Test
    public void blockByDailyThresoldTest() throws Exception {

		final Connection connection = getConnection();
		final Statement statement = connection.createStatement();
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:11.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:01:11.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:02:11.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:03:11.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:04:11.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 23:59:59.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		connection.commit();
    	
		final Threshold threshold = new Threshold(startDateString, "daily", "5");
		
        final List<BlockOccurrencesDto> blockOccurrencesDtos =
        	instance.blockByThresold(threshold);

        assertNotNull(blockOccurrencesDtos);
        assertFalse(blockOccurrencesDtos.isEmpty());
        assertEquals(1, blockOccurrencesDtos.size());
        assertEquals("192.168.98.21", blockOccurrencesDtos.get(0).getIp());
    }

}