package com.acguglielmo.accesslogmonitor.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApplicationStatusTest {

    private static final String TEST_FILENAME = "test.txt";

    private File file;
    private int linesWrittenToTheFile;

    @Before
    public void setUp() throws Exception {
        final Path path = Paths.get(TEST_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }

        final Path filePath = Files.createFile(path);
        final BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath, StandardOpenOption.WRITE);

        for (int i =0; i < 1000; i++) {
            bufferedWriter.write("\nA line in the text file.");
            linesWrittenToTheFile++;
        }
        bufferedWriter.close();
        this.file = filePath.toFile();
    }

    @Test
    public void getInstanceTest() throws Exception {
        final ApplicationStatus instance = ApplicationStatus.getInstance();

        Assert.assertNotNull(instance);
        Assert.assertEquals(instance, ApplicationStatus.getInstance());
    }

    @Test
    public void updateProgressByChunkTest() throws Exception {

        final ApplicationStatus instance = ApplicationStatus.getInstance();
        instance.setProgress(0);

        instance.configureChunkSize(file, 10);

        instance.updateProgressByChunk();
        Assert.assertEquals(1.0 * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE / 100,
                instance.getProgress(), 0.0001);

        for (int i = 1; i < 50; i++) {
            instance.updateProgressByChunk();
        }
        Assert.assertEquals(50.0 * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE / 100,
                instance.getProgress(), 0.0001);

        instance.updateProgressByChunk();
        Assert.assertEquals(51.0 * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE / 100,
                instance.getProgress(), 0.0001);
    }

    @Test
    public void configureChunkSizeTest() throws Exception {
        final ApplicationStatus instance = ApplicationStatus.getInstance();

        int batchChunkSize = 10;
        instance.configureChunkSize(file, batchChunkSize);
        Assert.assertEquals((double) batchChunkSize / linesWrittenToTheFile * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE,
                instance.getChunkSize(), 0.0001);

        batchChunkSize = 20;
        instance.configureChunkSize(file, batchChunkSize);
        Assert.assertEquals((double) batchChunkSize / linesWrittenToTheFile * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE,
                instance.getChunkSize(), 0.0001);

        batchChunkSize = 30;
        instance.configureChunkSize(file, batchChunkSize);
        Assert.assertEquals((double) batchChunkSize / linesWrittenToTheFile * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE,
                instance.getChunkSize(), 0.0001);

        batchChunkSize = 400;
        instance.configureChunkSize(file, batchChunkSize);
        Assert.assertEquals((double) batchChunkSize / linesWrittenToTheFile * ApplicationStatus.ESTIMATED_FILE_LOADING_TO_DATABASE_JOB_PERCENTAGE,
                instance.getChunkSize(), 0.0001);
    }

    @Test
    public void getProgressBarTest() throws Exception {
        final ApplicationStatus instance = ApplicationStatus.getInstance();

        instance.setProgress(10.0);
        Assert.assertEquals("|=====                                             | 10% Done", ApplicationStatus.getInstance().getProgressBar());

        instance.setProgress(15.0);
        Assert.assertEquals("|=======                                           | 15% Done", ApplicationStatus.getInstance().getProgressBar());

        instance.setProgress(70.0);
        Assert.assertEquals("|===================================               | 70% Done", ApplicationStatus.getInstance().getProgressBar());
    }

    @Test
    public void addFutureTest() throws Exception {
        final ApplicationStatus instance = ApplicationStatus.getInstance();

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Future<?> future = executorService.submit(new Thread());
        executorService.shutdownNow();
        instance.addFuture(future);

        Assert.assertTrue(instance.getFutureList().contains(future));
    }

    @Test
    public void getFutureListTest() throws Exception {

        final ApplicationStatus instance = ApplicationStatus.getInstance();

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        final Future<?> future = executorService.submit( () -> System.out.println());
        instance.addFuture(future);

        final List<Future<?>> futureList = instance.getFutureList();
        Assert.assertTrue(futureList.contains(future));
        Assert.assertEquals(1, futureList.size());

        final Future<?> future2 = executorService.submit(() -> System.out.println());
        instance.addFuture(future2);

        Assert.assertFalse(futureList.contains(future2));
        Assert.assertEquals(1, futureList.size());

        Assert.assertTrue(instance.getFutureList().contains(future2));
        Assert.assertEquals(2, instance.getFutureList().size());
        executorService.shutdownNow();
    }

    @After
    public void tearDown() throws Exception {
        final Path path = Paths.get(TEST_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }
    }
}