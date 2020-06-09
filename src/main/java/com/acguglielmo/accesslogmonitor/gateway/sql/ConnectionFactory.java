package com.acguglielmo.accesslogmonitor.gateway.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.agroal.api.AgroalDataSource;
import lombok.Setter;

@Setter
@ApplicationScoped
public class ConnectionFactory {

    @Inject
    AgroalDataSource dataSource;
    
    public Connection getConnection() throws SQLException {
        
        return dataSource.getConnection();
        
    }
}
