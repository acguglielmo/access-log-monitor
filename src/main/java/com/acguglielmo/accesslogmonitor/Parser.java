package com.acguglielmo.accesslogmonitor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;

import com.acguglielmo.accesslogmonitor.cli.CommandLineHelper;
import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.BlockOccurrencesGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;
import com.acguglielmo.accesslogmonitor.util.PropertiesHolder;
import com.acguglielmo.accesslogmonitor.util.Threshold;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.mysql.cj.jdbc.exceptions.MySQLTimeoutException;

public class Parser {

    protected static final String CONFIG_FILE_NOT_FOUND_MESSAGE = "Please provide a path to a config file or create a " +
	        "\"config.properties\" file in the working directory with the " +
	        "following properties filled according to your environment settings:" +
	        "\n db.connection.url=jdbc:mysql://<server>:<port>/<service_name> "+
	        "\n db.connection.username=<user> "+
	        "\n db.connection.password=<password>";
	
    List<BlockOccurrencesDto> blockOccurrencesDtos = new ArrayList<>();

    public static void main(final String[] args) {
        new Parser().process(args);
    }


	private void process(final String[] args) {

		new CommandLineHelper().configureCliOptions(args)
			.ifPresent(this::processAfterCliParametersConfigured);

    }


	private void processAfterCliParametersConfigured(final CommandLine commandLine) {
			
		final String configPath = commandLine.getOptionValue(CommandLineHelper.CONFIG_FILE_PATH, CommandLineHelper.CONFIG_FILE_DEFAULT_VALUE);
		
		try {
			PropertiesHolder.createInstance(configPath);
		} catch (final IOException e) {
			System.out.println(CONFIG_FILE_NOT_FOUND_MESSAGE);
			return;
		}
		
		checkIfDatabaseTablesExist();
		
		final ExecutorService executor = submitFileParsingTask(commandLine);
		
		monitorApplicationStatus(executor);
		
		if (!blockOccurrencesDtos.isEmpty()) {
			System.out.println(String.format("%-15s   %s", "IP", "Count"));
			blockOccurrencesDtos.forEach(System.out::println);
		}
	}


	private ExecutorService submitFileParsingTask(final CommandLine commandLine) {
		final String accessLogPath = commandLine.getOptionValue(CommandLineHelper.ACCESS_LOG_PATH, CommandLineHelper.FILENAME_DEFAULT_VALUE);

		final FileParsingTask task = new FileParsingTask(this, accessLogPath,
			new Threshold(
				commandLine.getOptionValue(CommandLineHelper.START_DATE),
				commandLine.getOptionValue(CommandLineHelper.DURATION),
				commandLine.getOptionValue(CommandLineHelper.THRESHOLD) ));
		
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(task);
		ApplicationStatus.getInstance().addFuture(future);
		executor.shutdown();
		return executor;
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
                if (runtimeException.getCause() instanceof SQLException) {
                    printSQLException(runtimeException);
                } else if (runtimeException.getCause() instanceof IOException) {
                    printIOException(runtimeException);
                } else {
                    System.out.println(e.getMessage());
                }
                return;
            }
        }
        System.out.println(e.getMessage());
    }

    private void printIOException(final RuntimeException runtimeException) {
        final IOException iOException = (IOException) runtimeException.getCause();
        System.out.println("An error occurred during a I/O operation: ");
        System.out.println(iOException.getMessage());
    }

    private void printSQLException(final RuntimeException runtimeException) {
        final SQLException sqlException = (SQLException) runtimeException.getCause();
        System.out.println("An error occurred during a database operation: ");
        if (sqlException instanceof CommunicationsException || sqlException instanceof MySQLTimeoutException) {
            System.out.println("Please check if the configured database server is up and running.");
        } else {
            System.out.println(sqlException.getMessage());
        }
    }
}
