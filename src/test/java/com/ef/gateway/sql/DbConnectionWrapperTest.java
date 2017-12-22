package com.ef.gateway.sql;

import com.ef.util.PropertiesHolder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.nio.file.*;
import java.sql.Connection;

import static org.junit.Assert.*;

public class DbConnectionWrapperTest {

    private static final String CONFIG_FILENAME = "config.properties";

    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
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
    public void getConnection() throws Exception {
        PropertiesHolder.destroyInstance();
        PropertiesHolder.createInstance(CONFIG_FILENAME);
        final DbConnectionWrapper dbConnectionWrapper = new DbConnectionWrapper();
        final Connection connection = dbConnectionWrapper.getConnection();

        assertNotNull(connection);
        assertEquals(connection, dbConnectionWrapper.getConnection());
        assertNotEquals(connection, new DbConnectionWrapper().getConnection());
    }

    @Test
    public void closeDbConnection() throws Exception {
        PropertiesHolder.destroyInstance();
        PropertiesHolder.createInstance(CONFIG_FILENAME);
        final DbConnectionWrapper dbConnectionWrapper = new DbConnectionWrapper();
        final Connection connection = dbConnectionWrapper.getConnection();

        assertNotNull(connection);
        assertTrue(connection.isValid(0));

        dbConnectionWrapper.closeDbConnection();
        assertTrue(connection.isClosed());
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
        final Path path = Paths.get(CONFIG_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }
    }

}