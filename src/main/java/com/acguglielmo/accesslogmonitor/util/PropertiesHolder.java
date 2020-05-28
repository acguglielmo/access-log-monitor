package com.acguglielmo.accesslogmonitor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class PropertiesHolder {

    public static final String DB_CONNECTION_PASSWORD = "db.connection.password";

    public static final String DB_CONNECTION_USERNAME = "db.connection.username";

    public static final String DB_CONNECTION_URL = "db.connection.url";

    private Properties prop;

    private static volatile PropertiesHolder instance;

    private PropertiesHolder(final String configPath) throws IOException {
        load(configPath);
    }

    public static void createInstance(final String configPath) throws IOException {
        if (instance == null) {
            synchronized (PropertiesHolder.class) {
                if (instance == null) {
                    instance = new PropertiesHolder(configPath);
                }
            }
        }
    }

    public static void destroyInstance() throws IOException {
        instance = null;
    }

    public static PropertiesHolder getInstance() {
        if (instance == null) {
            throw new RuntimeException("The instance has not been created yet!");
        }
        return instance;
    }

    private void load(final String configPath) throws IOException {

    	final Path path = Paths.get(configPath);

        try (final InputStream input = new FileInputStream(new File(path.toUri()))) {

        	prop = new Properties();
            prop.load(input);

        }

    }

    public String getProperty(final String propertyName) {
        return prop.getProperty(propertyName);
    }
}
