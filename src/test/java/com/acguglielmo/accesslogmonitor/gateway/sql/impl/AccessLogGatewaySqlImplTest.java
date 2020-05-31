package com.acguglielmo.accesslogmonitor.gateway.sql.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.acguglielmo.accesslogmonitor.AbstractComponentTest;
import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.threshold.HourlyThreshold;
import com.acguglielmo.accesslogmonitor.threshold.Threshold;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

@RunWith(MockitoJUnitRunner.class)
public class AccessLogGatewaySqlImplTest extends AbstractComponentTest {

	@Spy
	private ApplicationStatus applicationStatus;

	@InjectMocks
	private AccessLogGatewaySqlImpl instance;

	@Before
	public void before() {
		
		FixtureFactoryLoader.loadTemplates("com.acguglielmo.accesslogmonitor.template");
		
	}
	
    @Test
    public void insertTest() throws Exception {
        final Pattern quote = Pattern.compile(Pattern.quote("|"));
        final List<String[]> list = new ArrayList<>();
        list.add(quote.split("2017-01-01 23:59:56.907|192.168.167.234|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0"));
        list.add(quote.split("2017-01-01 23:59:57.335|192.168.25.244|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36\""));
        list.add(quote.split("2017-01-01 23:59:58.735|192.168.114.222|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:55.0) Gecko/20100101 Firefox/55.0\""));
        list.add(quote.split("2017-01-01 23:59:58.777|192.168.229.251|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36\""));
        list.add(quote.split("2017-01-01 23:59:58.791|192.168.229.98|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36\""));
        instance.insert(list);
    }

    @Test
    public void findTest() throws Exception {
        
		final Connection connection = getConnection();
		final Statement statement = connection.createStatement();
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:11.763','192.168.234.82','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:21.164','192.168.234.82','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:23.003','192.168.169.194','\"GET / HTTP/1.1\"',200,'\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.79 Safari/537.36 Edge/14.14393\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:40.554','192.168.234.82','\"GET / HTTP/1.1\"',200,'\"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0\"');");
		statement.executeUpdate("INSERT INTO access_log (date,ip,request,status,user_agent) VALUES ('2017-01-01 00:00:54.583','192.168.169.194','\"GET / HTTP/1.1\"',200,'\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.79 Safari/537.36 Edge/14.14393\"');");
		connection.commit();
    	
        final Threshold threshold = Fixture.from(HourlyThreshold.class).gimme("2017-01-01.00:00:00, 1");

        final List<BlockOccurrencesDto> blockOccurrencesDtos = instance.find(threshold);
        assertNotNull(blockOccurrencesDtos);
        assertEquals(2, blockOccurrencesDtos.size());
        assertEquals("192.168.234.82", blockOccurrencesDtos.get(0).getIp());
        assertEquals(new Integer(3), blockOccurrencesDtos.get(0).getCount());
        assertEquals("192.168.169.194", blockOccurrencesDtos.get(1).getIp());
        assertEquals(new Integer(2), blockOccurrencesDtos.get(1).getCount());
    }

}