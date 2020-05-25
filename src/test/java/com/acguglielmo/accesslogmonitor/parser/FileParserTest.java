package com.acguglielmo.accesslogmonitor.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;

import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;

public class FileParserTest {

    private static final String ACCESS_LOG_FILENAME = "src/test/resources/access.log";

    @Test
    public void loadFileToDatabaseTest() throws Exception {
        new FileParser().loadFileToDatabase(Paths.get(ACCESS_LOG_FILENAME).toFile());

        final List<Future<?>> futureList = ApplicationStatus.getInstance().getFutureList();
        assertFalse(futureList.isEmpty());
        assertEquals(5, futureList.size());
    }

}