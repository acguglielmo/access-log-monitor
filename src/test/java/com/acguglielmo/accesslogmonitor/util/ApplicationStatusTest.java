package com.acguglielmo.accesslogmonitor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

public class ApplicationStatusTest {

    private File file = Paths.get("src/test/resources/test.txt").toFile();
    
    private int linesWrittenToTheFile = 1000;

    @Test
    public void updateProgressByChunkTest() throws Exception {

        final ApplicationStatus instance = new ApplicationStatus();
        instance.setProgress(0);

        instance.configureChunkSize(file, 10);

        instance.updateProgressByChunk();
        assertEquals(1.0 * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE / 100,
                instance.getProgress(), 0.001);

        for (int i = 1; i < 50; i++) {
            instance.updateProgressByChunk();
        }
        assertEquals(50.0 * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE / 100,
                instance.getProgress(), 0.1);

        instance.updateProgressByChunk();
        assertEquals(51.0 * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE / 100,
                instance.getProgress(), 0.1);
    }

    @Test
    public void configureChunkSizeTest() throws Exception {
        final ApplicationStatus instance = new ApplicationStatus();

        int batchChunkSize = 10;
        instance.configureChunkSize(file, batchChunkSize);
        assertEquals((double) batchChunkSize / linesWrittenToTheFile * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE,
                instance.getChunkSize(), 0.1);

        batchChunkSize = 20;
        instance.configureChunkSize(file, batchChunkSize);
        assertEquals((double) batchChunkSize / linesWrittenToTheFile * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE,
                instance.getChunkSize(), 0.1);

        batchChunkSize = 30;
        instance.configureChunkSize(file, batchChunkSize);
        assertEquals((double) batchChunkSize / linesWrittenToTheFile * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE,
                instance.getChunkSize(), 0.1);

        batchChunkSize = 400;
        instance.configureChunkSize(file, batchChunkSize);
        assertEquals((double) batchChunkSize / linesWrittenToTheFile * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE,
                instance.getChunkSize(), 0.1);
    }

    @Test
    public void addFutureTest() throws Exception {
        final ApplicationStatus instance = new ApplicationStatus();

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Future<?> future = executorService.submit(new Thread());
        executorService.shutdownNow();
        instance.addFuture(future);

        assertTrue(instance.getFutureList().contains(future));
    }

    @Test
    public void getFutureListTest() throws Exception {

        final ApplicationStatus instance = new ApplicationStatus();

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        final Future<?> future = executorService.submit( () -> System.out.println());
        instance.addFuture(future);

        final List<Future<?>> futureList = instance.getFutureList();
        assertTrue(futureList.contains(future));
        assertEquals(1, futureList.size());

        final Future<?> future2 = executorService.submit(() -> System.out.println());
        instance.addFuture(future2);

        assertFalse(futureList.contains(future2));
        assertEquals(1, futureList.size());

        assertTrue(instance.getFutureList().contains(future2));
        assertEquals(2, instance.getFutureList().size());
        executorService.shutdownNow();
    }

}