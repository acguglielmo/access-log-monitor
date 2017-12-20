package com.ef.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * The type Application status.
 */
public class ApplicationStatus {

    private double progress;
    private double chunkSize;

    /**
     * The Estimated file loading to database job percentage.
     */
    static final Integer ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE = 80;

    /**
     * The constant JOB_PROGRESS_AFTER_TRUNCATE_TABLE.
     */
    public static final Integer JOB_PROGRESS_AFTER_TRUNCATE_TABLE = 10;
    /**
     * The constant JOB_PROGRESS_AFTER_LOADING_FILE_TO_DATABASE.
     */
    public static final Integer JOB_PROGRESS_AFTER_LOADING_FILE_TO_DATABASE = 90;
    /**
     * The constant JOB_PROGRESS_AFTER_COMPLETION.
     */
    public static final Integer JOB_PROGRESS_AFTER_COMPLETION = 100;


    private static volatile ApplicationStatus instance;

    private List<Future<?>> futureList;

    private ApplicationStatus() {
        futureList = new ArrayList<>();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
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

    /**
     * Update progress by chunk.
     */
    public synchronized void updateProgressByChunk() {
        this.progress += chunkSize;
    }

    /**
     * Sets progress.
     *
     * @param progress the progress
     */
    public synchronized void setProgress(double progress) {
        this.progress = progress;
    }

    /**
     * Gets progress.
     *
     * @return the progress
     */
    synchronized double getProgress() {
        return this.progress;
    }

    /**
     * Gets chunk size.
     *
     * @return the chunk size
     */
    synchronized double getChunkSize() {
        return this.chunkSize;
    }

    /**
     * Configure chunk size.
     *
     * @param file           the file
     * @param batchChunkSize the batch chunk size
     * @throws IOException the io exception
     */
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

    /**
     * Gets progress bar.
     *
     * @return the progress bar
     */
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

    /**
     * Add future.
     *
     * @param future the future
     */
    public synchronized void addFuture(final Future<?> future) {
        this.futureList.add(future);
    }

    /**
     * Gets future list.
     *
     * @return the future list
     */
    public synchronized List<Future<?>> getFutureList() {
        return new ArrayList<>(this.futureList);
    }
}
