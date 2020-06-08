package com.acguglielmo.accesslogmonitor.gateway.sql;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ConnectionFactoryTest {

	@Inject
	ConnectionFactory instance;
	
    @Test
    public void getConnectionTest() throws Exception {

    	final Connection connection = instance.getConnection();

        assertNotNull(connection);
        assertNotEquals(connection, instance.getConnection());
    }

}