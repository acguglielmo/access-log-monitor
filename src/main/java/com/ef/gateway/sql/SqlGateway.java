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
        dbConnectionString = getConnectionString(propertiesHolder);
        dbUser = propertiesHolder.getProperty(PropertiesHolder.DB_AUTH_USER);
        dbPassword = propertiesHolder.getProperty(PropertiesHolder.DB_AUTH_PASSWORD);
    }

    private String getConnectionString(final PropertiesHolder propertiesHolder) {
        final String server = propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_SERVER);
        final String port = propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_PORT);
        final String serviceName = propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_SERVICE_NAME);

        final StringBuilder sb = new StringBuilder();
        sb.append("jdbc:mysql://").append(server).append(":")
                .append(port).append("/").append(serviceName)
                .append("?useTimezone=true&serverTimezone=UTC&useSSL=false");
        return sb.toString();
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
