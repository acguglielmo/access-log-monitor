package com.ef.gateway.sql;

import com.ef.util.PropertiesHolder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 */
public class DbConnectionWrapper {

    private String dbConnectionUrl;
    private String dbUser;
    private String dbPassword;

    private Connection dbConnection;

    /**
     * Instantiates a new Sql gateway.
     */
    public DbConnectionWrapper() {
        final PropertiesHolder propertiesHolder = PropertiesHolder.getInstance();
        dbConnectionUrl = propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_URL);
        dbUser = propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_USERNAME);
        dbPassword = propertiesHolder.getProperty(PropertiesHolder.DB_CONNECTION_PASSWORD);
    }

    /**
     * Gets connection.
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    public Connection getConnection() throws SQLException {
        if (dbConnection == null || dbConnection.isClosed()) {
            dbConnection = DriverManager.getConnection(
                    dbConnectionUrl.contains("mysql") ? dbConnectionUrl.concat("?useTimezone=true&serverTimezone=UTC&useSSL=false")
                    : dbConnectionUrl, dbUser, dbPassword);
            return dbConnection;
        }
        return dbConnection;
    }

    /**
     * Close db connection.
     *
     * @throws SQLException the sql exception
     */
    public void closeDbConnection() throws SQLException {
        if (dbConnection != null) {
            dbConnection.close();
        }
    }
}
