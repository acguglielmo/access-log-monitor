package com.ef.gateway.sql;

import com.ef.util.PropertiesHolder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The type Sql gateway.
 */
public abstract class SqlGateway {

    private String dbConnectionString;
    private String dbUser;
    private String dbPassword;

    private Connection dbConnection;
    /**
     * The Prepared statement.
     */
    protected PreparedStatement preparedStatement;

    /**
     * Instantiates a new Sql gateway.
     */
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

    /**
     * Gets connection.
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    protected Connection getConnection() throws SQLException {
        if (dbConnection == null || dbConnection.isClosed()) {
            dbConnection = DriverManager.getConnection(
                    dbConnectionString, dbUser, dbPassword);
            return dbConnection;
        }
        return dbConnection;
    }

    /**
     * Close db connection.
     *
     * @throws SQLException the sql exception
     */
    protected void closeDbConnection() throws SQLException {
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (dbConnection != null) {
            dbConnection.close();
        }
    }

    /**
     * Table exists boolean.
     *
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public abstract boolean tableExists() throws SQLException;
}
