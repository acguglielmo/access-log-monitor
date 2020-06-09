package com.acguglielmo.accesslogmonitor;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.acguglielmo.accesslogmonitor.cli.ApplicationCommandLine;
import com.acguglielmo.accesslogmonitor.cli.CommandLineHelper;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;

@ExtendWith(MockitoExtension.class)
public class ParserTest {

	@Spy
	private ApplicationStatus applicationStatus;
	
	@InjectMocks
	private Parser instance;

	@RegisterExtension
    public ConsoleWatcherSupport appender = new ConsoleWatcherSupport(LogManager.getLogger(Parser.class));
	
	@Test
	@Disabled("This test forces a call to System.exit, breaking the build")
	public void shouldShowConfigFileNotFoundWhenNoConfigFileIsNotFoundTest() {
		
		final ApplicationCommandLine cli = prepareCommandLineMockBehavior("a path");
		
		instance.process(cli);
		
		assertEquals(
		  format("%s%n", Parser.CONFIG_FILE_NOT_FOUND_MESSAGE), 
		  appender.getOutput()
		);
		
	}

	@Test
	@Disabled("This test forces a call to System.exit, breaking the build")
	public void shouldHaltApplicationIfAnExceptionIsThrownByFileParsingTask() throws Exception {

		final Future<?> aFuture = mock(Future.class);

		when(aFuture.isDone()).thenReturn(true);

		when(aFuture.get()).thenThrow(new ExecutionException(new RuntimeException("error")));

		final List<Future<?>> futureList = Collections.singletonList(aFuture);

		when(applicationStatus.getFutureList())
			.thenReturn(futureList);

		final ApplicationCommandLine cli = prepareCommandLineMockBehavior("src/test/resources/application.properties");

		instance.process(cli);

	}

	private ApplicationCommandLine prepareCommandLineMockBehavior(final String configFilePath) {

		final ApplicationCommandLine commandLine = mock(ApplicationCommandLine.class);
		
		when(commandLine.getOptionValue(CommandLineHelper.CONFIG_FILE_PATH, CommandLineHelper.CONFIG_FILE_DEFAULT_VALUE))
			.thenReturn(configFilePath);

		return commandLine;
	}
}
