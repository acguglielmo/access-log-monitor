package com.acguglielmo.accesslogmonitor.util;

import org.junit.*;

import com.acguglielmo.accesslogmonitor.util.PropertiesHolder;

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
    @BeforeClass
    public static void setUp() throws Exception {
        final Path path = Paths.get(CONFIG_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }

        final Path filePath = Files.createFile(path);
        final BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath, StandardOpenOption.WRITE);

        bufferedWriter.write("\n" + PropertiesHolder.DB_CONNECTION_URL + "=jdbc:mysql://localhost:3306/MySQL"+
                "\n" + PropertiesHolder.DB_CONNECTION_USERNAME + "=test"+
                "\n" + PropertiesHolder.DB_CONNECTION_PASSWORD + "=passwd");

        bufferedWriter.close();
    }

    /**
     * Create instance without creating first test.
     *
     * @throws Exception the exception
     */
    @Test(expected = RuntimeException.class)
    public void getInstanceWithoutCreatingFirstTest() throws Exception {
        PropertiesHolder.destroyInstance();
        PropertiesHolder.getInstance();
    }

    /**
     * Get instance creating first test.
     *
     * @throws Exception the exception
     */
    @Test
    public void getInstanceCreatingFirstTest() throws Exception {
        PropertiesHolder.destroyInstance();
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
        PropertiesHolder.destroyInstance();
        PropertiesHolder.createInstance(CONFIG_FILENAME);
        final PropertiesHolder propertiesHolder = PropertiesHolder.getInstance();

        assertNotNull(propertiesHolder);
        assertEquals("jdbc:mysql://localhost:3306/MySQL", propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_URL));
        assertEquals("test", propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_USERNAME));
        assertEquals("passwd", propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_PASSWORD));
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