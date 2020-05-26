package com.acguglielmo.accesslogmonitor.cli;

import org.apache.commons.cli.CommandLine;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Optional;

public class CommandLineHelperTest {

    private CommandLineHelper instance;

    @Before
    public void setUp() throws Exception {
        this.instance = new CommandLineHelper();
    }

    @Test
    public void configureCliOptionsWithAllParametersCorrectlyFilledTest() {

    	final String[] args = new String[] {"--accessLog=access.log", "--configFile=config.properties", "--startDate=2017-01-01.00:00:00", "--duration=daily", "--threshold=500"};

    	final Optional<CommandLine> returnedValue = instance.configureCliOptions(args);

        assertNotNull(returnedValue);
        assertTrue(returnedValue.isPresent());

        final CommandLine commandLine = returnedValue.get();

        assertEquals("access.log", commandLine.getOptionValue(CommandLineHelper.ACCESS_LOG_PATH));
        assertEquals("config.properties", commandLine.getOptionValue(CommandLineHelper.CONFIG_FILE_PATH));
        assertEquals("2017-01-01.00:00:00", commandLine.getOptionValue(CommandLineHelper.START_DATE));
        assertEquals("daily", commandLine.getOptionValue(CommandLineHelper.DURATION));
        assertEquals("500", commandLine.getOptionValue(CommandLineHelper.THRESHOLD));
    }

    @Test
    public void configureCliOptionsWithDurationParameterIncorrectlyFilledTest() {

    	final String[] args = new String[] {"--accessLog=access.log", "--configFile=config.properties", "--startDate=2017-01-01.00:00:00", "--duration=daili", "--threshold=500"};

    	assertOptionalEmptyReturned(args);

    }

    @Test
    public void configureCliOptionsWithDurationParameterNotFilledTest() {

    	final String[] args = new String[] {"--accessLog=access.log", "--configFile=config.properties", "--startDate=2017-01-01.00:00:00", "--duration", "--threshold=500"};

    	assertOptionalEmptyReturned(args);

    }

    @Test
    public void configureCliOptionsWithOnlyAccessLogParameterTest() {

    	final String[] args = new String[] {"--accessLog=access.log"};

    	assertOptionalEmptyReturned(args);

    }
    
	private void assertOptionalEmptyReturned(final String[] args) {
		final Optional<CommandLine> returnedValue = instance.configureCliOptions(args);
		
    	assertNotNull(returnedValue);
    	
    	assertFalse(returnedValue.isPresent());
	}

}