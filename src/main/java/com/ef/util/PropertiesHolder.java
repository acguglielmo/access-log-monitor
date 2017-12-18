package com.ef.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        try {
            input = new FileInputStream(PROPERTIES_FILENAME);
            prop = new Properties();
            prop.load(input);
        } catch (final IOException ex) {
           System.out.println(ex.getMessage());
           System.out.println("The application will now exit.");
           System.exit(1);
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

    public String getProperty(final String propertyName) {
        return prop.getProperty(propertyName);
    }
}
