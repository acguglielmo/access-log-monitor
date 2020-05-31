package com.acguglielmo.accesslogmonitor.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;

@RunWith(MockitoJUnitRunner.class)
public class FileParserTest {

    private static final String ACCESS_LOG_FILENAME = "src/test/resources/access.log";

    @Spy
    private ApplicationStatus applicationStatus;

    @InjectMocks
    private FileParser instance;

    @Test
    public void loadFileToDatabaseTest() throws Exception {

    	instance.loadFileToDatabase(Paths.get(ACCESS_LOG_FILENAME).toFile());

        final List<Future<?>> futureList = applicationStatus.getFutureList();
        assertFalse(futureList.isEmpty());
        assertEquals(5, futureList.size());
    }

}