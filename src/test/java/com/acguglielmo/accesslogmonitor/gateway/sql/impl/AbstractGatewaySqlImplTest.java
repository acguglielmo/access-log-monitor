package com.acguglielmo.accesslogmonitor.gateway.sql.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class AbstractGatewaySqlImplTest {

    protected abstract void initDatabase() throws SQLException, ClassNotFoundException;

    protected Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.hsqldb.jdbcDriver");
        return DriverManager.getConnection("jdbc:hsqldb:mem:test", "sa", "");
    }
}
