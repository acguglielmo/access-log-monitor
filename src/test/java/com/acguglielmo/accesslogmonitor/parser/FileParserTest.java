package com.acguglielmo.accesslogmonitor.parser;

import com.acguglielmo.accesslogmonitor.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;

import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.Future;

import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.*;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {FileParser.class, FileParser.GatewayClient.class})
public class FileParserTest {

    private static FileParser instance;

    private static final String ACCESS_LOG_FILENAME = "access.log";

    private static final String LINE_1 = "2017-01-01 23:59:26.178|192.168.122.77|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko\"\n";
    private static final String LINE_2 = "2017-01-01 23:59:27.885|192.168.129.191|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36\"\n";
    private static final String LINE_3 = "2017-01-01 23:59:28.047|192.168.31.166|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 Edge/15.15063\"\n";
    private static final String LINE_4 = "2017-01-01 23:59:28.740|192.168.112.165|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:55.0) Gecko/20100101 Firefox/55.0\"\n";
    private static final String LINE_5 = "2017-01-01 23:59:29.749|192.168.70.119|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36\"\n";
    private static final String LINE_6 = "2017-01-01 23:59:31.128|192.168.110.220|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Linux; Android 7.0; Moto G (4) Build/NPJS25.93-14-8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.116 Mobile Safari/537.36\"";

    @BeforeClass
    public static void setUp() throws Exception {
        instance = new FileParser();
    }

    private File createFile() throws IOException {
        final Path path = Paths.get(ACCESS_LOG_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }

        final Path filePath = Files.createFile(path);
        final BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath, StandardOpenOption.WRITE);
        for(int i =0; i < 1001; i++) {
            bufferedWriter.write(LINE_1);
            bufferedWriter.write(LINE_2);
            bufferedWriter.write(LINE_3);
            bufferedWriter.write(LINE_4);
            bufferedWriter.write(LINE_5);
            bufferedWriter.write(LINE_6);
        }
        return filePath.toFile();
    }

    @Test
    public void loadFileToDatabaseTest() throws Exception {
        final AccessLogGatewaySqlImpl mockedGateway = createMock(AccessLogGatewaySqlImpl.class);

        expectNew(AccessLogGatewaySqlImpl.class).andReturn(mockedGateway);
        expectLastCall().once();

        replay(mockedGateway, AccessLogGatewaySqlImpl.class);

        instance.loadFileToDatabase(createFile());

        final List<Future<?>> futureList = ApplicationStatus.getInstance().getFutureList();
        assertFalse(futureList.isEmpty());
        assertEquals(5, futureList.size());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        final Path path = Paths.get(ACCESS_LOG_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }
    }
}