package com.acguglielmo.accesslogmonitor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * The type Properties holder.
 */
public final class PropertiesHolder {
    /**
     * The constant DB_CONNECTION_PASSWORD.
     */
    public static final String DB_CONNECTION_PASSWORD = "db.connection.password";
    /**
     * The constant DB_CONNECTION_USERNAME.
     */
    public static final String DB_CONNECTION_USERNAME = "db.connection.username";

    /**
     * The constant DB_CONNECTION_URL.
     */
    public static final String DB_CONNECTION_URL = "db.connection.url";

    private Properties prop;

    private static volatile PropertiesHolder instance;

    private PropertiesHolder(final String configPath) throws IOException {
        load(configPath);
    }

    /**
     * Create instance.
     *
     * @param configPath the config path
     * @throws IOException the io exception
     */
    public static void createInstance(final String configPath) throws IOException {
        if (instance == null) {
            synchronized (PropertiesHolder.class) {
                if (instance == null) {
                    instance = new PropertiesHolder(configPath);
                }
            }
        }
    }

    /**
     * Destroy instance.
     *
     * For JUnit tests.
     *
     * @throws IOException the io exception
     */
    public static void destroyInstance() throws IOException {
        instance = null;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static PropertiesHolder getInstance() {
        if (instance == null) {
            throw new RuntimeException("The instance has not been created yet!");
        }
        return instance;
    }

    /**
     * Loads the properties from the config file path
     *
     * @param configPath the path to the config file.
     */
    private void load(final String configPath) throws IOException {
        InputStream input = null;
        final Path path = Paths.get(configPath);
        try {
            input = new FileInputStream(new File(path.toUri()));
            prop = new Properties();
            prop.load(input);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Gets property.
     *
     * @param propertyName the property name
     * @return the property
     */
    public String getProperty(final String propertyName) {
        return prop.getProperty(propertyName);
    }
}
