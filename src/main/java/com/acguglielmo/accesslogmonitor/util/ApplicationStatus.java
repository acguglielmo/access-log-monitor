package com.acguglielmo.accesslogmonitor.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class ApplicationStatus {

    private double progress;
    private double chunkSize;

    static final Integer ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE = 90;

    public static final Integer JOB_PROGRESS_AFTER_COMPLETION = 100;

    private List<Future<?>> futureList;

    public ApplicationStatus() {
        futureList = new ArrayList<>();
    }


    public synchronized void updateProgressByChunk() {
        this.progress += chunkSize;
    }

    public synchronized void setProgress(double progress) {
        this.progress = progress;
    }

    synchronized double getProgress() {
        return this.progress;
    }

    synchronized double getChunkSize() {
        return this.chunkSize;
    }

    public synchronized void configureChunkSize(final File file, final Integer batchChunkSize) throws IOException {
        final LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
        while (lineNumberReader.skip(Long.MAX_VALUE) > 0)
        {
            // Loop just in case the file is > Long.MAX_VALUE or skip() decides to not read the entire file
        }
        this.chunkSize = batchChunkSize.doubleValue() / lineNumberReader.getLineNumber()
                * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE;
        lineNumberReader.close();
    }

    public String getProgressBar()
    {
        return ProgressBar.of(progress).toString();
    }

    public synchronized void addFuture(final Future<?> future) {
        this.futureList.add(future);
    }

    public synchronized List<Future<?>> getFutureList() {
        return new ArrayList<>(this.futureList);
    }
    
    protected synchronized void clearFutureList() {
    	this.futureList.clear();
    }
}
