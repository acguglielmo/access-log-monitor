package com.ef.cli;

import com.ef.enums.Duration;
import com.ef.util.DateUtils;
import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.List;

public class CliHelper {

    public static final String ACCESS_LOG_PATH = "accessLog";
    public static final String CONFIG_FILE_PATH = "configFile";
    public static final String START_DATE = "startDate";
    public static final String DURATION = "duration";
    public static final String THRESHOLD = "threshold";

    public static final String THRESHOLD_DEFAULT_VALUE = "100";
    public static final String FILENAME_DEFAULT_VALUE = "access.log";
    public static final String CONFIG_FILE_DEFAULT_VALUE = "config.properties";

    /**
     * Configures the CLI options
     *
     * @param args the args
     */
    public CommandLine configureCliOptions(final String[] args) {
        final Options options = new Options();
        options.addOption("a", ACCESS_LOG_PATH, true,
                "Path to log file. Default value is "+ FILENAME_DEFAULT_VALUE +" (in the working directory)");
        options.addOption("c", CONFIG_FILE_PATH, true,
                "Path to config file. Default value is "+ CONFIG_FILE_DEFAULT_VALUE +" (in the working directory)");
        options.addOption("t", THRESHOLD, true, "Threshold value to block. Only integer values. Default value is 100.");

        final Option startDateOption = new Option("s", START_DATE, true,
                "Required. Start date to analysis in the following format: yyyy-MM-dd.HH:mm:ss");
        startDateOption.setRequired(true);
        options.addOption(startDateOption);

        final ChoiceOption durationOption =
                new ChoiceOption("d", DURATION,  true, "Required. Options:",
                        true, Duration.HOURLY.getName(), Duration.DAILY.getName());
        options.addOption(durationOption);

        final CommandLineParser commandLineParser = new DefaultParser();
        try {
            final CommandLine commandLine = commandLineParser.parse(options, args);
            checkCommandLineArgs(commandLine);
            return commandLine;
        } catch (org.apache.commons.cli.ParseException | RuntimeException e) {
            printCliHelp(options);
            return null;
        }
    }

    private void checkCommandLineArgs(final CommandLine commandLine) throws RuntimeException {
        final List<Option> optionList = Arrays.asList(commandLine.getOptions());

        for (Option option : optionList) {
            if (option.getLongOpt().equals(START_DATE)) {
                DateUtils.DATE_FORMAT_ARGS.parse(option.getValue());
            }

            if (option.getLongOpt().equals(THRESHOLD)) {
                if (option.getValue() != null) {
                    Integer.parseInt(option.getValue());
                }
            }

            if (option.getLongOpt().equals(DURATION)) {
                ((ChoiceOption) option).checkChoiceValue();
            }
        }
    }

    /**
     * Generate the help statement.
     *
     * @param options the options
     */
    private static void printCliHelp(Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "parser", options );
    }
}
