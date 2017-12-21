package com.ef.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedWriter;
import java.nio.file.*;

import static org.junit.Assert.*;

/**
 * The type Properties holder test.
 */
public class PropertiesHolderTest {

    private static final String CONFIG_FILENAME = "config.properties";

    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        final Path path = Paths.get(CONFIG_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }

        final Path filePath = Files.createFile(path);
        final BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath, StandardOpenOption.WRITE);

        bufferedWriter.write("\n" + PropertiesHolder.DB_CONNECTION_SERVER + "=localhost"+
                "\n" + PropertiesHolder.DB_CONNECTION_PORT + "=3306"+
                "\n" + PropertiesHolder.DB_CONNECTION_SERVICE_NAME + "=MySQL"+
                "\n" + PropertiesHolder.DB_AUTH_USER + "=test"+
                "\n" + PropertiesHolder.DB_AUTH_PASSWORD + "=passwd");

        bufferedWriter.close();
    }

    /**
     * Create instance without creating first test.
     *
     * Ignored for now because as JUnit framework does not promote test ordering,
     * we should create a subclass of {@link java.lang.ClassLoader}
     * and implement the dynamic class reloading.
     *
     * @throws Exception the exception
     */
    @Test(expected = RuntimeException.class)
    @Ignore
    public void createInstanceWithoutCreatingFirstTest() throws Exception {
        PropertiesHolder.getInstance();
    }

    /**
     * Create instance creating first test.
     *
     * @throws Exception the exception
     */
    @Test
    public void createInstanceCreatingFirstTest() throws Exception {
        PropertiesHolder.createInstance(CONFIG_FILENAME);
        final PropertiesHolder propertiesHolder = PropertiesHolder.getInstance();

        assertNotNull(propertiesHolder);

        PropertiesHolder.createInstance(CONFIG_FILENAME);
        assertEquals(propertiesHolder, PropertiesHolder.getInstance());
    }


    /**
     * Gets property test.
     *
     * @throws Exception the exception
     */
    @Test
    public void getPropertyTest() throws Exception {
        PropertiesHolder.createInstance(CONFIG_FILENAME);
        final PropertiesHolder propertiesHolder = PropertiesHolder.getInstance();

        assertNotNull(propertiesHolder);
        assertEquals("localhost", propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_SERVER));
        assertEquals("3306", propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_PORT));
        assertEquals("MySQL", propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_SERVICE_NAME));
        assertEquals("test", propertiesHolder.getProperty(PropertiesHolder.DB_AUTH_USER));
        assertEquals("passwd", propertiesHolder.getProperty(PropertiesHolder.DB_AUTH_PASSWORD));
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown() throws Exception {
        final Path path = Paths.get(CONFIG_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }
    }
}