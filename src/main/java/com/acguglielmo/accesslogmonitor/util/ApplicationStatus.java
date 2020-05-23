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


    private static volatile ApplicationStatus instance;

    private List<Future<?>> futureList;

    private ApplicationStatus() {
        futureList = new ArrayList<>();
    }

    public static ApplicationStatus getInstance() {
        if (instance == null) {
            synchronized (ApplicationStatus.class) {
                if (instance == null) {
                    instance = new ApplicationStatus();
                }
            }
        }
        return instance;
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
        final StringBuilder sb = new StringBuilder();

        long x = (long) progress / 2;
        sb.append("|");
        for (int k = 0; k < 50; k++)
            sb.append((x <= k) ? " " : "=");
        sb.append("| ");
        sb.append((long) progress);
        sb.append("% Done");

        return sb.toString();
    }

    public synchronized void addFuture(final Future<?> future) {
        this.futureList.add(future);
    }

    public synchronized List<Future<?>> getFutureList() {
        return new ArrayList<>(this.futureList);
    }
}
