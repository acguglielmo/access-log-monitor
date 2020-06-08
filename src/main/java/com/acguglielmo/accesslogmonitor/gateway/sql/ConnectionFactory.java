package com.acguglielmo.accesslogmonitor.gateway.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Setter;

@Setter
@ApplicationScoped
public class ConnectionFactory {

    @ConfigProperty(name = "db.connection.password")
    String dbConnectionPassword;

    @ConfigProperty(name = "db.connection.username")
    String dbConnectionUsername;

    @ConfigProperty(name = "db.connection.url")
    String dbConnectionUrl;
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
        		dbConnectionUrl.contains("mysql") ? dbConnectionUrl.concat("?useTimezone=true&serverTimezone=UTC&useSSL=false")
                : dbConnectionUrl, dbConnectionUsername, dbConnectionPassword);
    }
}
