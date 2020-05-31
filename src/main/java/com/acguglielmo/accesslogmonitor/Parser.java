package com.acguglielmo.accesslogmonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.acguglielmo.accesslogmonitor.cli.ApplicationCommandLine;
import com.acguglielmo.accesslogmonitor.cli.CommandLineHelper;
import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.exception.ExceptionHandler;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;
import com.acguglielmo.accesslogmonitor.util.PropertiesHolder;

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

	private final CommandLineHelper commandLineHelper;

	public static void main(final String[] args) {

		new Parser(
        	new CommandLineHelper()
        ).process(args);

	}

	void process(final String[] args) {

		commandLineHelper.configureCliOptions(args)
			.ifPresent(this::processAfterCliParametersConfigured);

    }


	private void processAfterCliParametersConfigured(final ApplicationCommandLine commandLine) {
			
		buildProperties(commandLine).ifPresent(e -> {
			
			final ExecutorService executor = submitFileParsingTask(commandLine);
			
			monitorApplicationStatus(executor);
			
			if (!blockOccurrencesDtos.isEmpty()) {
				System.out.println(String.format("%-15s   %s", "IP", "Count"));
				blockOccurrencesDtos.forEach(System.out::println);
			}
			
		});

	}

	private Optional<PropertiesHolder> buildProperties(final CommandLine commandLine) {
		
		final String configPath = commandLine.getOptionValue(CommandLineHelper.CONFIG_FILE_PATH, CommandLineHelper.CONFIG_FILE_DEFAULT_VALUE);
		
		try {
			PropertiesHolder.createInstance(configPath);
			return Optional.of(PropertiesHolder.getInstance());
		} catch (final IOException e) {
			LOGGER.error(CONFIG_FILE_NOT_FOUND_MESSAGE);
			return Optional.empty();
		}
	}

	private ExecutorService submitFileParsingTask(final ApplicationCommandLine commandLine) {

		final FileParsingTask task = new FileParsingTask(this, commandLine);

		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(task);
		ApplicationStatus.getInstance().addFuture(future);
		executor.shutdown();
		return executor;
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
