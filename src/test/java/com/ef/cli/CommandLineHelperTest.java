package com.ef.cli;

import org.apache.commons.cli.CommandLine;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * The type Cli helper test.
 */
public class CommandLineHelperTest {

    private CommandLineHelper instance;

    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        this.instance = CommandLineHelper.getInstance();
    }

    /**
     * Gets instance test.
     *
     * @throws Exception the exception
     */
    @Test
    public void getInstanceTest() throws Exception {
        assertEquals(instance, CommandLineHelper.getInstance());
    }

    /**
     * Configure cli options with all parameters correctly filled test.
     */
    @Test
    public void configureCliOptionsWithAllParametersCorrectlyFilledTest() {
        final String[] args = new String[] {"--accessLog=access.log", "--configFile=config.properties", "--startDate=2017-01-01.00:00:00", "--duration=daily", "--threshold=500"};
        final CommandLine commandLine = instance.configureCliOptions(args);
        assertNotNull(commandLine);
        assertEquals("access.log", commandLine.getOptionValue(CommandLineHelper.ACCESS_LOG_PATH));
        assertEquals("config.properties", commandLine.getOptionValue(CommandLineHelper.CONFIG_FILE_PATH));
        assertEquals("2017-01-01.00:00:00", commandLine.getOptionValue(CommandLineHelper.START_DATE));
        assertEquals("daily", commandLine.getOptionValue(CommandLineHelper.DURATION));
        assertEquals("500", commandLine.getOptionValue(CommandLineHelper.THRESHOLD));
    }

    /**
     * Configure cli options with duration parameter incorrectly filled test.
     */
    @Test
    public void configureCliOptionsWithDurationParameterIncorrectlyFilledTest() {
        final String[] args = new String[] {"--accessLog=access.log", "--configFile=config.properties", "--startDate=2017-01-01.00:00:00", "--duration=daili", "--threshold=500"};
        assertNull(instance.configureCliOptions(args));
    }

    /**
     */
    @Test
    public void configureCliOptionsWithDurationParameterNotFilledTest() {
        final String[] args = new String[] {"--accessLog=access.log", "--configFile=config.properties", "--startDate=2017-01-01.00:00:00", "--duration", "--threshold=500"};
        assertNull(instance.configureCliOptions(args));
    }

    /**
     * Configure cli options test.
     */
    @Test
    public void configureCliOptionsWithOnlyAccessLogParameterTest() {
        final String[] args = new String[] {"--accessLog=access.log"};
        assertNull(instance.configureCliOptions(args));
    }

}