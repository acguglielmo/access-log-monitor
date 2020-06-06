package com.acguglielmo.accesslogmonitor.gateway.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import com.acguglielmo.accesslogmonitor.util.PropertiesHolder;

public class ConnectionFactoryTest {

    private static final String CONFIG_FILENAME = "src/test/resources/application.properties";

    @Test
    public void getInstanceTest() throws Exception {
        PropertiesHolder.destroyInstance();
        PropertiesHolder.createInstance(CONFIG_FILENAME);
        final ConnectionFactory instance = ConnectionFactory.getInstance();

        assertNotNull(instance);
        assertEquals(instance, ConnectionFactory.getInstance());
    }

    @Test
    public void getConnectionTest() throws Exception {
        PropertiesHolder.destroyInstance();
        PropertiesHolder.createInstance(CONFIG_FILENAME);
        final Connection connection = ConnectionFactory.getInstance().getConnection();

        assertNotNull(connection);
        assertNotEquals(connection, ConnectionFactory.getInstance().getConnection());
    }

}