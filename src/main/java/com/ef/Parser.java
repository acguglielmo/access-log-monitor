package com.ef;

import com.ef.analysis.Analyzer;
import com.ef.cli.CliHelper;
import com.ef.parser.FileParser;
import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * The type Parser.
 */
public class Parser {

	private static final String DEFAULT_LOG_FILENAME = "access.log";

	public static void main(final String[] args) {

		final Date startTime = new Date();

		final CommandLine commandLine = new CliHelper().configureCliOptions(args);

		if (commandLine ==  null) {
			return;
		}

		final Path path = getPath(commandLine);

        new FileParser().parseFile(path);

        new Analyzer().analyze(
                commandLine.getOptionValue(CliHelper.START_DATE),
                commandLine.getOptionValue(CliHelper.DURATION),
                commandLine.getOptionValue(CliHelper.THRESHOLD));

        System.out.println("Time elapsed: " + (new Date().getTime() - startTime.getTime()));
	}

	private static Path getPath(final CommandLine commandLine) {
		if (commandLine.hasOption(CliHelper.ACCESS_LOG)) {
			return Paths.get(commandLine.getOptionValue(CliHelper.ACCESS_LOG));
		} else {
			return Paths.get(DEFAULT_LOG_FILENAME);
		}
	}
}
