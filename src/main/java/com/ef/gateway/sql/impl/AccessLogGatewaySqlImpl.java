package com.ef.gateway.sql.impl;

import com.ef.gateway.AccessLogGateway;
import com.ef.gateway.sql.SqlGateway;

import java.sql.*;
import java.util.List;

public class AccessLogGatewaySqlImpl extends SqlGateway implements AccessLogGateway {

    public AccessLogGatewaySqlImpl() {
        super();
    }

    @Override
    public void insert(final List<String[]> dataList) {

        try {
            final String statement = "INSERT INTO usr_log.access_log"
                    + "(date, ip, request, status, user_agent) VALUES"
                    + "(?,?,?,?,?)";
            preparedStatement = getConnection().prepareStatement(statement);

            for (final String data[] : dataList){
                preparedStatement.setString(1, data[0]);
                preparedStatement.setString(2, data[1]);
                preparedStatement.setString(3, data[2]);
                preparedStatement.setString(4, data[3]);
                preparedStatement.setString(5, data[4]);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            super.closeDbConnection();
        }
    }
}
