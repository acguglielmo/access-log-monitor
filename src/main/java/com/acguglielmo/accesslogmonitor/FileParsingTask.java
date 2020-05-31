package com.acguglielmo.accesslogmonitor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.acguglielmo.accesslogmonitor.analysis.Analyzer;
import com.acguglielmo.accesslogmonitor.cli.ApplicationCommandLine;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.gateway.sql.impl.BlockOccurrencesGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.parser.FileParser;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class FileParsingTask implements Runnable {

	private final Parser parser;
	private final ApplicationCommandLine commandLine;
	private final ApplicationStatus applicationStatus;

    @Override
    public void run() {
        try {
            final Path path = Paths.get(commandLine.getFilePath());
            final File file = new File(path.toUri());

            applicationStatus.configureChunkSize(file, FileParser.MAX_BATCH_CHUNK_SIZE);

            new FileParser(applicationStatus).loadFileToDatabase(file);

            this.parser.blockOccurrencesDtos = 
            	new Analyzer(
            		new AccessLogGatewaySqlImpl(applicationStatus),
            		new BlockOccurrencesGatewaySqlImpl())
            			.blockByThresold(commandLine.to());
            
            applicationStatus.setProgress(ApplicationStatus.JOB_PROGRESS_AFTER_COMPLETION);

        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

}