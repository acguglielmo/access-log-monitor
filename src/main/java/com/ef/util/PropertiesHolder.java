package com.ef.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class PropertiesHolder {
    public static final String DB_AUTH_PASSWORD = "db.auth.password";
    public static final String DB_AUTH_USER = "db.auth.user";

    public static final String DB_CONNECTION_SERVER = "db.connection.server";
    public static final String DB_CONNECTION_PORT ="db.connection.port";
    public static final String DB_CONNECTION_SERVICE_NAME = "db.connection.servicename";

    private Properties prop;

    private static volatile PropertiesHolder instance;

    private PropertiesHolder(final String configPath){
        load(configPath);
    }

    public static void createInstance(final String configPath) {
        if (instance == null) {
            synchronized (PropertiesHolder.class) {
                if (instance == null) {
                    instance = new PropertiesHolder(configPath);
                }
            }
        }
    }

    public static PropertiesHolder getInstance() {
        return instance;
    }

    /**
     * Loads the properties from the config file path
     *
     * @param configPath the path to the config file.
     */
    private void load(final String configPath){
        InputStream input = null;
        final Path path = Paths.get(configPath);
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
