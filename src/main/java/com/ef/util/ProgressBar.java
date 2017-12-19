package com.ef.util;

import java.io.*;

public class ProgressBar {

    private double progress;
    private double chunkSize;

    public static Integer ESTIMATED_LINE_PARSING_TO_DATABASE_JOB_PERCENTAGE = 80;

    private static volatile ProgressBar instance;

    private ProgressBar(){}

    public static ProgressBar getInstance() {
        if (instance == null) {
            synchronized (ProgressBar.class) {
                if (instance == null) {
                    instance = new ProgressBar();
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

    public synchronized void configureChunkSize(final File file, final Integer batchChunkSize) throws IOException {
        final LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
        while (lineNumberReader.skip(Long.MAX_VALUE) > 0)
        {
            // Loop just in case the file is > Long.MAX_VALUE or skip() decides to not read the entire file
        }
        this.chunkSize = batchChunkSize.doubleValue() / lineNumberReader.getLineNumber()
                * ProgressBar.ESTIMATED_LINE_PARSING_TO_DATABASE_JOB_PERCENTAGE;
    }

    public void displayBar()
    {
        StringBuilder sb = new StringBuilder();

        long x = (long) progress / 2;
        sb.append("|");
        for (int k = 0; k < 50; k++)
            sb.append((x <= k) ? " " : "=");
        sb.append("|");

        System.out.printf("\r%s %s%% Done", sb.toString(), (long) progress);
    }
}
