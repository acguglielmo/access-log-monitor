package com.ef.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesHolder {
    public static final String DB_PASSWORD = "db.password";
    public static final String DB_CONNECTION = "db.connection";
    public static final String DB_USER = "db.user";

    private static final String PROPERTIES_FILENAME = "config.properties";

    private Properties prop = new Properties();

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

    private void load(){
        InputStream input = null;
        try {
            input = PropertiesHolder.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME);
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
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
