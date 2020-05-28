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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.acguglielmo.accesslogmonitor.cli.CommandLineHelper;
import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.exception.ExceptionHandler;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.BlockOccurrencesGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;
import com.acguglielmo.accesslogmonitor.util.PropertiesHolder;
import com.acguglielmo.accesslogmonitor.util.Threshold;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Parser {

	private static final Logger LOGGER = LogManager.getLogger(Parser.class);
	
    protected static final String CONFIG_FILE_NOT_FOUND_MESSAGE = "Please provide a path to a config file or create a " +
	        "\"config.properties\" file in the working directory with the " +
	        "following properties filled according to your environment settings:" +
	        "\n db.connection.url=jdbc:mysql://<server>:<port>/<service_name> "+
	        "\n db.connection.username=<user> "+
	        "\n db.connection.password=<password>";
	
    List<BlockOccurrencesDto> blockOccurrencesDtos = new ArrayList<>();

	private final AccessLogGatewaySqlImpl accessLogGatewaySqlImpl;

	private final BlockOccurrencesGatewaySqlImpl blockOccurrencesGatewaySqlImpl;

	public static void main(final String[] args) {

		new Parser(
        	new AccessLogGatewaySqlImpl(),
        	new BlockOccurrencesGatewaySqlImpl()
        ).process(args);

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
			LOGGER.error(CONFIG_FILE_NOT_FOUND_MESSAGE);
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
        	accessLogGatewaySqlImpl.tableExists();
        	blockOccurrencesGatewaySqlImpl.tableExists();
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
                	ExceptionHandler.printExceptionToConsole(e);

                    System.out.println("The application will now exit.");
                    executor.shutdownNow();
                    System.exit(1);
                }
            }
        }
    }

}
