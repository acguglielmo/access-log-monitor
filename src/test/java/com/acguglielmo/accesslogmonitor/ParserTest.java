package com.acguglielmo.accesslogmonitor;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.acguglielmo.accesslogmonitor.cli.ApplicationCommandLine;
import com.acguglielmo.accesslogmonitor.cli.CommandLineHelper;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;
import com.acguglielmo.accesslogmonitor.util.PropertiesHolder;

@RunWith(MockitoJUnitRunner.class)
public class ParserTest {

	@Spy
	private ApplicationStatus applicationStatus;
	
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
		
		final ApplicationCommandLine commandLine = Mockito.mock(ApplicationCommandLine.class);
		
		when(commandLineHelper.configureCliOptions(any()))
			.thenReturn(Optional.of(commandLine));
		
		when(commandLine.getOptionValue(CommandLineHelper.CONFIG_FILE_PATH, CommandLineHelper.CONFIG_FILE_DEFAULT_VALUE))
			.thenReturn("path");
		
		instance.process(new String[] {});
		
		assertEquals(
		  format("%s%n", Parser.CONFIG_FILE_NOT_FOUND_MESSAGE), 
		  appender.getOutput()
		);
		
	}
	
}
