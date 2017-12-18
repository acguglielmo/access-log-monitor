package com.ef.cli;

import com.ef.enums.Duration;
import org.apache.commons.cli.*;

import java.util.Arrays;

public class CliHelper {

    public static final String ACCESS_LOG = "accesslog";
    public static final String START_DATE = "startDate";
    public static final String DURATION = "duration";
    public static final String THRESHOLD = "threshold";

    /**
     * Configures the CLI options
     *
     * @param args the args
     */
    public CommandLine configureCliOptions(final String[] args) {
        final Options options = new Options();
        options.addOption("a", ACCESS_LOG, true, "Log file location. If not provided, " +
                "will search for access.log in the working directory");
        options.addOption("s", START_DATE, true, "Start date to analysis");
        options.addOption("t", THRESHOLD, true, "Threshold");

        final ChoiceOption choiceOption =
                new ChoiceOption("d", DURATION,  true, "Options: ",
                        Duration.HOURLY.getName(), Duration.DAILY.getName());
        options.addOption(choiceOption);

        final CommandLineParser commandLineParser = new DefaultParser();
        try {
            final CommandLine commandLine = commandLineParser.parse(options, args);
            checkDurationArgs(commandLine);
            return commandLine;
        } catch (org.apache.commons.cli.ParseException | RuntimeException e) {
            printCliHelp(options);
            return null;
        }
    }

    private void checkDurationArgs(final CommandLine commandLine) {
        Arrays.stream(commandLine.getOptions())
                .filter(option -> option.getLongOpt().equals(DURATION))
                .map(option -> (ChoiceOption) option)
                .forEach(ChoiceOption::checkChoiceValue);
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
