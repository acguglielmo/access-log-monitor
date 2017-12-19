package com.ef;

import com.ef.analysis.Analyzer;
import com.ef.cli.CliHelper;
import com.ef.dto.BlockOccurrencesDto;
import com.ef.enums.Duration;
import com.ef.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.ef.parser.FileParser;
import com.ef.util.ProgressBar;
import com.ef.util.PropertiesHolder;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Parser.
 */
public class Parser {

    private List<BlockOccurrencesDto> blockOccurrencesDtos = new ArrayList<>();

    public static void main(final String[] args) {
        new Parser().process(args);
    }


	private void process(final String[] args) {
		final CommandLine commandLine = new CliHelper().configureCliOptions(args);
		if (commandLine ==  null) {
			return;
		}

		final String accessLogPath = commandLine.getOptionValue(CliHelper.ACCESS_LOG_PATH, CliHelper.FILENAME_DEFAULT_VALUE);
        final String configPath = commandLine.getOptionValue(CliHelper.CONFIG_FILE_PATH, CliHelper.CONFIG_FILE_DEFAULT_VALUE);

        PropertiesHolder.createInstance(configPath);

        final Path path = Paths.get(accessLogPath);
        final File file = new File(path.toUri());
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(file);

            final BufferedReader bufferedReader = new BufferedReader(fileReader);

            ProgressBar.getInstance().configureChunkSize(file, FileParser.MAX_BATCH_CHUNK_SIZE);

            final Task task = new Task(bufferedReader,
                    Integer.parseInt(commandLine.getOptionValue(CliHelper.THRESHOLD, CliHelper.THRESHOLD_DEFAULT_VALUE)),
                    commandLine.getOptionValue(CliHelper.START_DATE),
                    commandLine.getOptionValue(CliHelper.DURATION));

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(task);
            executor.shutdown();

            displayProgressBar(executor);

            blockOccurrencesDtos.forEach(System.out::println);

        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private void displayProgressBar(final ExecutorService executor) {
        while(!executor.isTerminated()) {
            try {
                Thread.sleep(200);
                ProgressBar.getInstance().displayBar();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ProgressBar.getInstance().displayBar();
        System.out.println();
    }

    private class Task implements Runnable {
        private BufferedReader bufferedReader;
        private Integer threshold;
        private String startDate;
        private Duration duration;

        private Task(final BufferedReader bufferedReader, final Integer threshold,
                     final String startDate, final String duration ) {
            this.bufferedReader = bufferedReader;
            this.threshold = threshold;
            this.startDate = startDate;
            this.duration = Duration.getByName(duration);
        }

        @Override
        public void run() {
            new AccessLogGatewaySqlImpl().truncate();
            ProgressBar.getInstance().setProgress(5);

            FileParser.getInstance().loadFileToDatabase(bufferedReader);
            ProgressBar.getInstance().setProgress(90);

            blockOccurrencesDtos = Analyzer.getInstance()
                    .blockByThresold(startDate, duration, threshold);
            ProgressBar.getInstance().setProgress(100);
        }
    }
}
