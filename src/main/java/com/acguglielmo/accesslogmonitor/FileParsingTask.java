package com.acguglielmo.accesslogmonitor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.acguglielmo.accesslogmonitor.analysis.Analyzer;
import com.acguglielmo.accesslogmonitor.parser.FileParser;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;
import com.acguglielmo.accesslogmonitor.util.Threshold;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class FileParsingTask implements Runnable {

	private final Parser parser;
	private String accessLogPath;
    private Threshold threshold;

    @Override
    public void run() {
        try {
            final Path path = Paths.get(accessLogPath);
            final File file = new File(path.toUri());

            ApplicationStatus.getInstance().configureChunkSize(file, FileParser.MAX_BATCH_CHUNK_SIZE);

            new FileParser().loadFileToDatabase(file);

            this.parser.blockOccurrencesDtos = Analyzer.getInstance().blockByThresold(threshold);
            
            ApplicationStatus.getInstance().setProgress(ApplicationStatus.JOB_PROGRESS_AFTER_COMPLETION);

        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }
}