package com.acguglielmo.accesslogmonitor.gateway.sql;

import com.acguglielmo.accesslogmonitor.gateway.sql.ConnectionFactory;
import com.acguglielmo.accesslogmonitor.util.PropertiesHolder;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.nio.file.*;
import java.sql.Connection;

import static org.junit.Assert.*;

public class ConnectionFactoryTest {

    private static final String CONFIG_FILENAME = "config.properties";

    @BeforeClass
    public static void setUp() throws Exception {
        final Path path = Paths.get(CONFIG_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }

        final Path filePath = Files.createFile(path);
        final BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath, StandardOpenOption.WRITE);

        bufferedWriter.write("\n" + PropertiesHolder.DB_CONNECTION_URL + "=jdbc:hsqldb:mem:test"+
                "\n" + PropertiesHolder.DB_CONNECTION_USERNAME + "=sa"+
                "\n" + PropertiesHolder.DB_CONNECTION_PASSWORD + "=");

        bufferedWriter.close();
    }

    @Test
    public void getInstanceTest() throws Exception {
        PropertiesHolder.destroyInstance();
        PropertiesHolder.createInstance(CONFIG_FILENAME);
        final ConnectionFactory instance = ConnectionFactory.getInstance();

        assertNotNull(instance);
        assertEquals(instance, ConnectionFactory.getInstance());
    }

    @Test
    public void getConnectionTest() throws Exception {
        PropertiesHolder.destroyInstance();
        PropertiesHolder.createInstance(CONFIG_FILENAME);
        final Connection connection = ConnectionFactory.getInstance().getConnection();

        assertNotNull(connection);
        assertNotEquals(connection, ConnectionFactory.getInstance().getConnection());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        final Path path = Paths.get(CONFIG_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }
    }

}