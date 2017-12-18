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
        dbConnectionString = PropertiesHolder.getInstance().getProperty(PropertiesHolder.DB_CONNECTION) + "?useTimezone=true&serverTimezone=UTC&useSSL=false";
        dbUser = PropertiesHolder.getInstance().getProperty(PropertiesHolder.DB_USER);
        dbPassword = PropertiesHolder.getInstance().getProperty(PropertiesHolder.DB_PASSWORD);
    }

    protected Connection getConnection() {
        try {
            if (dbConnection == null || dbConnection.isClosed()) {
                dbConnection = DriverManager.getConnection(
                        dbConnectionString, dbUser, dbPassword);
                return dbConnection;
            }
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
