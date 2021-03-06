package com.acguglielmo.accesslogmonitor.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.gateway.sql.ConnectionFactory;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.BlockOccurrencesGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.threshold.DailyThreshold;
import com.acguglielmo.accesslogmonitor.threshold.HourlyThreshold;
import com.acguglielmo.accesslogmonitor.threshold.Threshold;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;

@QuarkusTest
public class AnalyzerTest {

	@Inject
	ConnectionFactory connectionFactory;
	
	@InjectSpy
    private AccessLogGatewaySqlImpl accessLogGatewaySqlImpl;
    
	@InjectSpy
    private BlockOccurrencesGatewaySqlImpl blockOccurrencesGatewaySqlImpl;
	
	@Inject
    Analyzer instance;

    @AfterEach
    @BeforeEach
    public void cleanUp() throws Exception {
    	
		final Connection connection = connectionFactory.getConnection();
		final Statement statement = connection.createStatement();
    	
		statement.executeUpdate("DELETE FROM access_log;");
		connection.commit();
		
    }

    @BeforeEach
	public void before() {
		
		FixtureFactoryLoader.loadTemplates("com.acguglielmo.accesslogmonitor.template");

	}

    @Test
    public void shouldBlockIpAddressThatExceededHourlyThresoldTest() throws Exception {

		final Connection connection = connectionFactory.getConnection();
		final Statement statement = connection.createStatement();
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:11.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:01:11.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:02:11.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:03:11.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:04:11.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:05:00.000','192.168.98.20','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		connection.commit();

		final Threshold threshold = Fixture.from(HourlyThreshold.class).gimme("2017-01-01.00:00:00, 1");
		
        final List<BlockOccurrencesDto> blockOccurrencesDtos =
        	instance.blockByThresold(threshold);

        assertNotNull(blockOccurrencesDtos);
        assertFalse(blockOccurrencesDtos.isEmpty());
        assertEquals(1, blockOccurrencesDtos.size());
        assertEquals("192.168.98.20", blockOccurrencesDtos.get(0).getIp());
    }


    @Test
    public void shouldBlockIpAddressThatExceededDailyThresoldTest() throws Exception {

		final Connection connection = connectionFactory.getConnection();
		final Statement statement = connection.createStatement();
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:11.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:01:11.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:02:11.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:03:11.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:04:11.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 23:59:59.000','192.168.98.21','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		connection.commit();

		final Threshold threshold = Fixture.from(DailyThreshold.class).gimme("2017-01-01.00:00:00, 5");

        final List<BlockOccurrencesDto> blockOccurrencesDtos =
        	instance.blockByThresold(threshold);

        assertNotNull(blockOccurrencesDtos);
        assertFalse(blockOccurrencesDtos.isEmpty());
        assertEquals(1, blockOccurrencesDtos.size());
        assertEquals("192.168.98.21", blockOccurrencesDtos.get(0).getIp());
    }
    
    @Test
    public void shouldThrowRuntimeExceptionWhenSQLExceptionOccursTest() throws Exception {
    	
    	final SQLException sqlException = new SQLException();
		
    	doThrow(sqlException).when(accessLogGatewaySqlImpl).find(any());
    	
    	try {
    	
    		instance.blockByThresold(Fixture.from(DailyThreshold.class).gimme("2017-01-01.00:00:00, 5"));
    		
    		fail("A RuntimeException should have been thrown!");
    	
    	} catch (final RuntimeException runtimeException) {
    		
    		assertEquals(sqlException, runtimeException.getCause());

    	} catch (final Exception exception) {
    		
    		fail("A RuntimeException should have been thrown!");
    		
    	}
    }

}