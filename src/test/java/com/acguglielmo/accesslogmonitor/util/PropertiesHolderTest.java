package com.acguglielmo.accesslogmonitor.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedWriter;
import java.nio.file.*;

public class PropertiesHolderTest {
    
    private static final String CONFIG_FILENAME = "config.properties";

    @BeforeAll
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

    @Test
    public void getInstanceWithoutCreatingFirstTest() throws Exception {
        PropertiesHolder.destroyInstance();
        
        try {
            
            PropertiesHolder.getInstance();
        
        } catch (final RuntimeException e) {
            
            assertNotNull(e.getMessage());
        
        }
    }

    @Test
    public void getInstanceCreatingFirstTest() throws Exception {
        PropertiesHolder.destroyInstance();
        PropertiesHolder.createInstance(CONFIG_FILENAME);
        final PropertiesHolder propertiesHolder = PropertiesHolder.getInstance();

        assertNotNull(propertiesHolder);

        PropertiesHolder.createInstance(CONFIG_FILENAME);
        assertEquals(propertiesHolder, PropertiesHolder.getInstance());
    }


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

    @AfterAll
    public static void tearDown() throws Exception {
        final Path path = Paths.get(CONFIG_FILENAME);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(path);
        }
    }
}