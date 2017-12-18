package com.ef.gateway.sql;

import com.ef.util.PropertiesHolder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SqlGateway {

    private String dbConnectionString;
    private String dbUser;
    private String dbPassword;

    private Connection dbConnection;
    protected PreparedStatement preparedStatement;

    public SqlGateway() {
        final PropertiesHolder propertiesHolder = PropertiesHolder.getInstance();
        dbConnectionString = propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION) + "?useTimezone=true&serverTimezone=UTC&useSSL=false";
        dbUser = propertiesHolder.getProperty(PropertiesHolder.DB_USER);
        dbPassword = propertiesHolder.getProperty(PropertiesHolder.DB_PASSWORD);
    }

    protected Connection getConnection() {
        try {
            if (dbConnection == null || dbConnection.isClosed()) {
                dbConnection = DriverManager.getConnection(
                        dbConnectionString, dbUser, dbPassword);
                return dbConnection;
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    protected void closeDbConnection() {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
