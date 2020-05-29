package com.acguglielmo.accesslogmonitor;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.acguglielmo.accesslogmonitor.cli.ApplicationCommandLine;
import com.acguglielmo.accesslogmonitor.cli.CommandLineHelper;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.BlockOccurrencesGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.util.PropertiesHolder;

@RunWith(MockitoJUnitRunner.class)
public class ParserTest {

	@Mock
	private AccessLogGatewaySqlImpl accessLogGatewaySqlImpl;

	@Mock
	private BlockOccurrencesGatewaySqlImpl blockOccurrencesGatewaySqlImpl;
	
	@Mock
	private CommandLineHelper commandLineHelper;
	
	@InjectMocks
	private Parser instance;

	@Rule
    public ConsoleWatcherSupport appender = new ConsoleWatcherSupport(LogManager.getLogger(Parser.class));
	
    @Before
    public void before() throws Exception {
    	
    	PropertiesHolder.destroyInstance();
    	
    }
    
	@Test
	public void shouldDoNothingWhenNoArgsAreProvidedTest() {

		instance.process(null);

		assertEquals("", appender.getOutput());
	}
	
	@Test
	public void shouldShowConfigFileNotFoundWhenNoConfigFileIsProvidedTest() {
		
		final String[] args = 
			new String[] {"--accessLog=access.log", "--configFile=config.properties", "--startDate=2017-01-01.00:00:00", "--duration=daily", "--threshold=500"};

		final ApplicationCommandLine commandLine = Mockito.mock(ApplicationCommandLine.class);
		
		when(commandLineHelper.configureCliOptions(args))
			.thenReturn(Optional.of(commandLine));
		
		when(commandLine.getOptionValue(CommandLineHelper.CONFIG_FILE_PATH, CommandLineHelper.CONFIG_FILE_DEFAULT_VALUE))
			.thenReturn("path");
		
		instance.process(args);
		
		assertEquals(
		  format("%s%n", Parser.CONFIG_FILE_NOT_FOUND_MESSAGE), 
		  appender.getOutput()
		);
		
	}
	
}
