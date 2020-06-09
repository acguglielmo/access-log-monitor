package com.acguglielmo.accesslogmonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.acguglielmo.accesslogmonitor.cli.ApplicationCommandLine;
import com.acguglielmo.accesslogmonitor.cli.CommandLineHelper;
import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.exception.ExceptionHandler;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@Singleton
@QuarkusMain
public class Parser implements QuarkusApplication {

    protected static final String CONFIG_FILE_NOT_FOUND_MESSAGE = "Please provide a path to a config file or create a " +
	        "\"config.properties\" file in the working directory with the " +
	        "following properties filled according to your environment settings:" +
	        "\n db.connection.url=jdbc:mysql://<server>:<port>/<service_name> "+
	        "\n db.connection.username=<user> "+
	        "\n db.connection.password=<password>";
	
    List<BlockOccurrencesDto> blockOccurrencesDtos = new ArrayList<>();

    @Inject
	ApplicationStatus applicationStatus;
    
    @Inject
    Instance<FileParsingTask> fileParsingTasks;

    public static void main(String[] args) {
        
        Quarkus.run(Parser.class, args);
        
    }
    
	@Override
	public int run(final String... args) {

		new CommandLineHelper().configureCliOptions(args)
			.ifPresent( this::process );

		return 1;

	}

	void process(final ApplicationCommandLine commandLine) {
			
//		buildProperties(commandLine).ifPresent(e -> {
			
			final ExecutorService executor = submitFileParsingTask(commandLine);
			
			monitorApplicationStatus(executor);
			
			if (!blockOccurrencesDtos.isEmpty()) {
				System.out.println(String.format("%-15s   %s", "IP", "Count"));
				blockOccurrencesDtos.forEach(System.out::println);
			}
			
//		});

	}

//	private Optional<PropertiesHolder> buildProperties(final CommandLine commandLine) {
//		
//		final String configPath = commandLine.getOptionValue(CommandLineHelper.CONFIG_FILE_PATH, CommandLineHelper.CONFIG_FILE_DEFAULT_VALUE);
//		
//		try {
//			PropertiesHolder.createInstance(configPath);
//			return Optional.of(PropertiesHolder.getInstance());
//		} catch (final IOException e) {
//			LOGGER.error(CONFIG_FILE_NOT_FOUND_MESSAGE);
//			return Optional.empty();
//		}
//	}

	private ExecutorService submitFileParsingTask(final ApplicationCommandLine commandLine) {

		final FileParsingTask task = fileParsingTasks.get().configure(commandLine);

		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(task);
		applicationStatus.addFuture(future);
		executor.shutdown();
		return executor;
	}

    private void monitorApplicationStatus(final ExecutorService executor) {

    	while(!executor.isTerminated()) {

            System.out.printf("\r%s ", applicationStatus.getProgressBar());

            for (final Future<?> future : applicationStatus.getFutureList()) {
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

    	System.out.printf("\r%s ", applicationStatus.getProgressBar());
        System.out.println();
    }

}
