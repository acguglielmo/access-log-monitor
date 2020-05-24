package com.acguglielmo.accesslogmonitor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.acguglielmo.accesslogmonitor.analysis.Analyzer;
import com.acguglielmo.accesslogmonitor.enums.Duration;
import com.acguglielmo.accesslogmonitor.parser.FileParser;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;

class FileParsingTask implements Runnable {
    /**
	 * 
	 */
	private final Parser parser;
	private String accessLogPath;
    private Integer threshold;
    private String startDate;
    private Duration duration;

    FileParsingTask(Parser parser, final String accessLogPath, final Integer threshold,
                          final String startDate, final String duration ) {
        this.parser = parser;
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

            new FileParser().loadFileToDatabase(file);

            this.parser.blockOccurrencesDtos = Analyzer.getInstance()
                    .blockByThresold(startDate, duration, threshold);
            ApplicationStatus.getInstance().setProgress(ApplicationStatus.JOB_PROGRESS_AFTER_COMPLETION);

        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }
}