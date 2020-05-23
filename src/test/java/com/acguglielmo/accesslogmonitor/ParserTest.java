package com.acguglielmo.accesslogmonitor;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParserTest {

	private final PrintStream originalOut = System.out;
	
	private final PrintStream originalErr = System.err;

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	
	@Before
	public void before() {
		
		System.setOut(new PrintStream(outContent));
		
		System.setErr(new PrintStream(errContent));
		
	}

	@After
	public void after() {

		System.setOut(originalOut);

	    System.setErr(originalErr);

	}
	
	@Test
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
			outContent.toString());
		
	}
	
	@Test
	public void shouldShowConfigFileNotFoundWhenNoConfigFileIsProvidedTest() {
		
		final String[] args = 
			new String[] {"--accessLog=access.log", "--configFile=config.properties", "--startDate=2017-01-01.00:00:00", "--duration=daily", "--threshold=500"};
		
		Parser.main(args);
		
		assertEquals(Parser.CONFIG_FILE_NOT_FOUND_MESSAGE + "\r\n", outContent.toString());
		
	}
	
}
