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



	public static void main(final String[] args) {

		final Date startTime = new Date();

		final CommandLine commandLine = new CliHelper().configureCliOptions(args);
		if (commandLine ==  null) {
			return;
		}

		final Path path = Paths.get(commandLine.getOptionValue(CliHelper.ACCESS_LOG, CliHelper.FILENAME_DEFAULT_VALUE));

        FileParser.getInstance().parseFile(path);

        Analyzer.getInstance().blockByThresold(
                commandLine.getOptionValue(CliHelper.START_DATE),
                commandLine.getOptionValue(CliHelper.DURATION),
                Integer.parseInt(commandLine.getOptionValue(CliHelper.THRESHOLD, CliHelper.THRESHOLD_DEFAULT_VALUE)));

        System.out.println("Time elapsed: " + (new Date().getTime() - startTime.getTime()));
	}
}
