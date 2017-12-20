package com.ef;

import com.ef.analysis.Analyzer;
import com.ef.cli.CliHelper;
import com.ef.dto.BlockOccurrencesDto;
import com.ef.enums.Duration;
import com.ef.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.ef.gateway.sql.impl.BlockOccurrencesGatewaySqlImpl;
import com.ef.parser.FileParser;
import com.ef.util.ApplicationStatus;
import com.ef.util.PropertiesHolder;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The type Parser.
 */
public class Parser {

    private List<BlockOccurrencesDto> blockOccurrencesDtos = new ArrayList<>();

    public static void main(final String[] args) {
        new Parser().process(args);
    }


	private void process(final String[] args) {
		final CommandLine commandLine = CliHelper.getInstance().configureCliOptions(args);
		if (commandLine ==  null) {
			return;
		}

		final String accessLogPath = commandLine.getOptionValue(CliHelper.ACCESS_LOG_PATH, CliHelper.FILENAME_DEFAULT_VALUE);
        final String configPath = commandLine.getOptionValue(CliHelper.CONFIG_FILE_PATH, CliHelper.CONFIG_FILE_DEFAULT_VALUE);

        PropertiesHolder.createInstance(configPath);

        checkIfDatabaseTablesExist();

        final Path path = Paths.get(accessLogPath);
        final File file = new File(path.toUri());

        try {
            ApplicationStatus.getInstance().configureChunkSize(file, FileParser.MAX_BATCH_CHUNK_SIZE);

            final Task task = new Task(file,
                    Integer.parseInt(commandLine.getOptionValue(CliHelper.THRESHOLD, CliHelper.THRESHOLD_DEFAULT_VALUE)),
                    commandLine.getOptionValue(CliHelper.START_DATE),
                    commandLine.getOptionValue(CliHelper.DURATION));

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<?> future = executor.submit(task);
            ApplicationStatus.getInstance().addFuture(future);
            executor.shutdown();

            monitorApplicationStatus(executor);

            blockOccurrencesDtos.forEach(System.out::println);

        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkIfDatabaseTablesExist() {
        try {
            new AccessLogGatewaySqlImpl().tableExists();
            new BlockOccurrencesGatewaySqlImpl().tableExists();
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void monitorApplicationStatus(final ExecutorService executor) {
        while(!executor.isTerminated()) {
            try {
                Thread.sleep(200);
                ApplicationStatus.getInstance().getProgressBar();
                System.out.printf("\r%s ", ApplicationStatus.getInstance().getProgressBar());

                verifyApplicationStatus(executor);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("\r%s ", ApplicationStatus.getInstance().getProgressBar());
        System.out.println();
    }

    private void verifyApplicationStatus(final ExecutorService executor) {
        final List<Future<?>> futureList = ApplicationStatus.getInstance().getFutureList();
        for (final Future<?> future : futureList) {
            if (future.isDone()) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println();
                    System.out.println(e.getMessage());
                    System.out.println("The application will now exit.");
                    executor.shutdownNow();
                    System.exit(1);
                }
            }
        }
    }

    private class Task implements Runnable {
        private File file;
        private Integer threshold;
        private String startDate;
        private Duration duration;

        private Task(final File file, final Integer threshold,
                     final String startDate, final String duration ) {
            this.file = file;
            this.threshold = threshold;
            this.startDate = startDate;
            this.duration = Duration.getByName(duration);
        }

        @Override
        public void run() {
            try {
                new AccessLogGatewaySqlImpl().truncate();
                ApplicationStatus.getInstance().setProgress(ApplicationStatus.JOB_PROGRESS_AFTER_TRUNCATE_TABLE);

                FileParser.getInstance().loadFileToDatabase(file);
                ApplicationStatus.getInstance().setProgress(ApplicationStatus.JOB_PROGRESS_AFTER_LOADING_FILE_TO_DATABASE);

                blockOccurrencesDtos = Analyzer.getInstance()
                        .blockByThresold(startDate, duration, threshold);
                ApplicationStatus.getInstance().setProgress(ApplicationStatus.JOB_PROGRESS_AFTER_COMPLETION);

            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
