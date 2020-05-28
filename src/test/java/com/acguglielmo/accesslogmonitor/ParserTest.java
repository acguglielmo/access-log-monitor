package com.acguglielmo.accesslogmonitor;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.acguglielmo.accesslogmonitor.util.PropertiesHolder;

public class ParserTest {

    @Rule 
    public ConsoleWatcherSupport appender = new ConsoleWatcherSupport(LogManager.getLogger(Parser.class));
	
    @Before
    public void before() throws Exception {
    	
    	PropertiesHolder.destroyInstance();
    	
    }
    
	@Test
	@Ignore("Verify if it is possible for Apache Cli to print help to a logger")
	public void shouldShowHelpMessageWhenNoArgsAreProvidedTest() {

		Parser.main(null);
		
		assertEquals("usage: parser\r\n" + 
			" -a,--accessLog <arg>    Path to log file. Default value is access.log (in\r\n" + 
			"                         the working directory)\r\n" + 
			" -c,--configFile <arg>   Path to config file. Default value is\r\n" + 
			"                         config.properties (in the working directory)\r\n" + 
			" -d,--duration <arg>     Required. Options: [hourly, daily]\r\n" + 
			" -s,--startDate <arg>    Required. Start date to analysis in the following\r\n" + 
			"                         format: yyyy-MM-dd.HH:mm:ss\r\n" + 
			" -t,--threshold <arg>    Required. Threshold value to block. Only integer\r\n" + 
			"                         values.\r\n" + 
			"", 
			appender.getOutput());
		
	}
	
	@Test
	public void shouldShowConfigFileNotFoundWhenNoConfigFileIsProvidedTest() {
		
		final String[] args = 
			new String[] {"--accessLog=access.log", "--configFile=config.properties", "--startDate=2017-01-01.00:00:00", "--duration=daily", "--threshold=500"};
		
		Parser.main(args);
		
		assertEquals(Parser.CONFIG_FILE_NOT_FOUND_MESSAGE + "\r\n", appender.getOutput());
		
	}

	@Test
	@Ignore
	public void test() {

		final String[] args = 
			new String[] {"--accessLog=access.log", "--configFile=src/test/resources/application.properties", "--startDate=2017-01-01.00:00:00", "--duration=daily", "--threshold=500"};

		Parser.main(args);

		
	}
	
}
