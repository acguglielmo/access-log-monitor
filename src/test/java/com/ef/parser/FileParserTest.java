package com.ef.parser;

import com.ef.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.ef.util.ApplicationStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.*;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {FileParser.class, FileParser.GatewayClient.class})
public class FileParserTest {

    private FileParser instance;

    private static final String ACCESS_LOG_FILENAME = "access.log";

    private static final String LINE_1 = "2017-01-01 23:59:26.178|192.168.122.77|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko\"\n";
    private static final String LINE_2 = "2017-01-01 23:59:27.885|192.168.129.191|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36\"\n";
    private static final String LINE_3 = "2017-01-01 23:59:28.047|192.168.31.166|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 Edge/15.15063\"\n";
    private static final String LINE_4 = "2017-01-01 23:59:28.740|192.168.112.165|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:55.0) Gecko/20100101 Firefox/55.0\"\n";
    private static final String LINE_5 = "2017-01-01 23:59:29.749|192.168.70.119|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36\"\n";
    private static final String LINE_6 = "2017-01-01 23:59:31.128|192.168.110.220|\"GET / HTTP/1.1\"|200|\"Mozilla/5.0 (Linux; Android 7.0; Moto G (4) Build/NPJS25.93-14-8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.116 Mobile Safari/537.36\"";

    private File file;

    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        this.instance = FileParser.getInstance();

        createFile();
    }

    private void createFile() throws IOException {
        final Path path = Paths.get(ACCESS_LOG_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }

        final Path filePath = Files.createFile(path);
        final BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath, StandardOpenOption.WRITE);
        bufferedWriter.write(LINE_1);
        bufferedWriter.write(LINE_2);
        bufferedWriter.write(LINE_3);
        bufferedWriter.write(LINE_4);
        bufferedWriter.write(LINE_5);
        bufferedWriter.write(LINE_6);
        bufferedWriter.close();
        this.file = filePath.toFile();
    }

    @Test
    public void getInstanceTest() throws Exception {
        assertEquals(instance, FileParser.getInstance());
    }

    @Test
    public void loadFileToDatabaseTest() throws Exception {
        final String quote = Pattern.quote("|");


        final List<String[]> lines = new ArrayList<>();
        lines.add(quote.split(LINE_1));
        lines.add(quote.split(LINE_2));
        lines.add(quote.split(LINE_3));
        lines.add(quote.split(LINE_4));
        lines.add(quote.split(LINE_5));
        lines.add(quote.split(LINE_6));

        final AccessLogGatewaySqlImpl mockedClient = createMock(AccessLogGatewaySqlImpl.class);

        expectNew(AccessLogGatewaySqlImpl.class).andReturn(mockedClient);
        expectLastCall().once();

        replay(mockedClient, AccessLogGatewaySqlImpl.class);

        instance.loadFileToDatabase(file);

        final List<Future<?>> futureList = ApplicationStatus.getInstance().getFutureList();
        assertFalse(futureList.isEmpty());
        assertEquals(1, futureList.size());
    }
}