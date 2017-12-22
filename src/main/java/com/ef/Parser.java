package com.ef;

import com.ef.analysis.Analyzer;
import com.ef.cli.CommandLineHelper;
import com.ef.dto.BlockOccurrencesDto;
import com.ef.enums.Duration;
import com.ef.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.ef.gateway.sql.impl.BlockOccurrencesGatewaySqlImpl;
import com.ef.parser.FileParser;
import com.ef.util.ApplicationStatus;
import com.ef.util.PropertiesHolder;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.mysql.cj.jdbc.exceptions.MySQLTimeoutException;
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

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(final String[] args) {
        new Parser().process(args);
    }


	private void process(final String[] args) {
		final CommandLine commandLine = CommandLineHelper.getInstance().configureCliOptions(args);
		if (commandLine ==  null) {
			return;
		}

		final String accessLogPath = commandLine.getOptionValue(CommandLineHelper.ACCESS_LOG_PATH, CommandLineHelper.FILENAME_DEFAULT_VALUE);
        final String configPath = commandLine.getOptionValue(CommandLineHelper.CONFIG_FILE_PATH, CommandLineHelper.CONFIG_FILE_DEFAULT_VALUE);

        try {
            PropertiesHolder.createInstance(configPath);
        } catch (final IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Please provide a path to a config file or create a " +
                    "\"config.properties\" file in the working directory with the " +
                    "following properties filled according to your environment settings:" +
                    "\n db.connection.url=jdbc:mysql://<server>:<port>/<service_name> "+
                    "\n db.connection.username=<user> "+
                    "\n db.connection.password=<password>");
            System.exit(1);
        }

        checkIfDatabaseTablesExist();

        final FileParsingTask task = new FileParsingTask(accessLogPath,
                Integer.parseInt(commandLine.getOptionValue(CommandLineHelper.THRESHOLD)),
                commandLine.getOptionValue(CommandLineHelper.START_DATE),
                commandLine.getOptionValue(CommandLineHelper.DURATION));

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<?> future = executor.submit(task);
        ApplicationStatus.getInstance().addFuture(future);
        executor.shutdown();

        monitorApplicationStatus(executor);

        if (!blockOccurrencesDtos.isEmpty()) {
            System.out.println(String.format("%-15s   %s", "IP", "Count"));
            blockOccurrencesDtos.forEach(System.out::println);
        }
    }

    private void checkIfDatabaseTablesExist() {
        try {
            new AccessLogGatewaySqlImpl().tableExists();
            new BlockOccurrencesGatewaySqlImpl().tableExists();
        } catch (final SQLException | ClassNotFoundException e) {
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
                    printExceptionToConsole(e);

                    System.out.println("The application will now exit.");
                    executor.shutdownNow();
                    System.exit(1);
                }
            }
        }
    }

    private void printExceptionToConsole(final Exception e) {
        System.out.println();

        if (e.getCause() != null && e.getCause() instanceof RuntimeException) {
            final RuntimeException runtimeException = (RuntimeException) e.getCause();
            if (runtimeException.getCause() != null) {
                printSQLException(runtimeException);
                printIOException(runtimeException);
            }
        } else {
            System.out.println(e.getMessage());
        }
    }

    private void printIOException(final RuntimeException runtimeException) {
        if (runtimeException.getCause() instanceof IOException) {
            final IOException iOException = (IOException) runtimeException.getCause();
            System.out.println("An error occurred during a I/O operation: ");
            System.out.println(iOException.getMessage());
        }
    }

    private void printSQLException(final RuntimeException runtimeException) {
        if (runtimeException.getCause() instanceof SQLException) {
            final SQLException sqlException = (SQLException) runtimeException.getCause();
            System.out.println("An error occurred during a database operation: ");
            if (sqlException instanceof CommunicationsException || sqlException instanceof MySQLTimeoutException) {
                System.out.println("Please check if the configured database server is up and running.");
            } else {
                System.out.println(sqlException.getMessage());
            }
        }
    }

    private class FileParsingTask implements Runnable {
        private String accessLogPath;
        private Integer threshold;
        private String startDate;
        private Duration duration;

        private FileParsingTask(final String accessLogPath, final Integer threshold,
                              final String startDate, final String duration ) {
            this.accessLogPath = accessLogPath;
            this.threshold = threshold;
            this.startDate = startDate;
            this.duration = Duration.getByName(duration);
        }

        @Override
        public void run() {
            try {
                final Path path = Paths.get(accessLogPath);
                final File file = new File(path.toUri());

                ApplicationStatus.getInstance().configureChunkSize(file, FileParser.MAX_BATCH_CHUNK_SIZE);

                FileParser.getInstance().loadFileToDatabase(file);

                blockOccurrencesDtos = Analyzer.getInstance()
                        .blockByThresold(startDate, duration, threshold);
                ApplicationStatus.getInstance().setProgress(ApplicationStatus.JOB_PROGRESS_AFTER_COMPLETION);

            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
