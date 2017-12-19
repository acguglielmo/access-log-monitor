package com.ef.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class PropertiesHolder {
    public static final String DB_PASSWORD = "db.password";
    public static final String DB_CONNECTION = "db.connection";
    public static final String DB_USER = "db.user";

    private static final String PROPERTIES_FILENAME = "config.properties";

    private Properties prop;

    private static PropertiesHolder instance;

    private PropertiesHolder(){
        load();
    }

    public static PropertiesHolder getInstance() {
        if (instance == null) {
            synchronized (PropertiesHolder.class) {
                if (instance == null) {
                    instance = new PropertiesHolder();
                }
            }
        }
        return instance;
    }

    /**
     * Loads the properties from the file {@link PropertiesHolder#PROPERTIES_FILENAME}
     * located in the working directory.
     */
    private void load(){
        InputStream input = null;
        final Path path = Paths.get(PROPERTIES_FILENAME);
        boolean shouldExit = false;
        try {
            input = new FileInputStream(new File(path.toUri()));
            prop = new Properties();
            prop.load(input);
        } catch (final IOException ex) {
            System.out.println(ex.getMessage());
            shouldExit = true;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (shouldExit) {
                exit();
            }
        }
    }

    private void exit() {
        System.out.println("The application will now exit.");
        System.exit(1);
    }

    public String getProperty(final String propertyName) {
        return prop.getProperty(propertyName);
    }
}
