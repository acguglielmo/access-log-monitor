package com.acguglielmo.accesslogmonitor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.acguglielmo.accesslogmonitor.analysis.Analyzer;
import com.acguglielmo.accesslogmonitor.cli.ApplicationCommandLine;
import com.acguglielmo.accesslogmonitor.parser.FileParser;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;

@ApplicationScoped
class FileParsingTask implements Runnable {

	@Inject
	Parser parser;
	
	@Inject
	FileParser fileParser;
	
	@Inject
	Analyzer analyzer;

	private ApplicationCommandLine commandLine;

	public FileParsingTask configure(final ApplicationCommandLine commandLine) {
		
		this.commandLine = commandLine;
		
		return this;
		
	}
	
	@Override
    public void run() {
        try {
            final Path path = Paths.get(commandLine.getFilePath());
            final File file = new File(path.toUri());

            parser.applicationStatus.configureChunkSize(file, FileParser.MAX_BATCH_CHUNK_SIZE);

            fileParser.loadFileToDatabase(file);

            this.parser.blockOccurrencesDtos = 
            	analyzer.blockByThresold(commandLine.to());
            
            parser.applicationStatus.setProgress(ApplicationStatus.JOB_PROGRESS_AFTER_COMPLETION);

        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

}