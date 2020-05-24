package com.acguglielmo.accesslogmonitor.parser;

import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.acguglielmo.accesslogmonitor.gateway.sql.impl.AccessLogGatewaySqlImpl;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {FileParser.class, FileParser.GatewayClient.class})
public class FileParserTest {

    private static FileParser instance;

    private static final String ACCESS_LOG_FILENAME = "src/test/resources/access.log";

    @BeforeClass
    public static void setUp() throws Exception {
        instance = new FileParser();
    }

    @Test
    public void loadFileToDatabaseTest() throws Exception {
        final AccessLogGatewaySqlImpl mockedGateway = createMock(AccessLogGatewaySqlImpl.class);

        expectNew(AccessLogGatewaySqlImpl.class).andReturn(mockedGateway);
        expectLastCall().once();

        replay(mockedGateway, AccessLogGatewaySqlImpl.class);

        instance.loadFileToDatabase(Paths.get(ACCESS_LOG_FILENAME).toFile());

        final List<Future<?>> futureList = ApplicationStatus.getInstance().getFutureList();
        assertFalse(futureList.isEmpty());
        assertEquals(5, futureList.size());
    }

}