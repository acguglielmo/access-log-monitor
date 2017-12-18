package com.ef.gateway.sql.impl;

import com.ef.dto.AccessLogDto;
import com.ef.gateway.AccessLogGateway;
import com.ef.gateway.sql.SqlGateway;

import java.sql.*;

public class AccessLogGatewaySqlImpl extends SqlGateway implements AccessLogGateway {

    public AccessLogGatewaySqlImpl() {
        super();
    }

    @Override
    public void insert(final AccessLogDto accessLogDto) {

        final String insertTableSQL = "INSERT INTO usr_log.access_log"
                + "(date, ip, request, status, user_agent) VALUES"
                + "(?,?,?,?,?)";

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = getConnection().prepareStatement(insertTableSQL);

            preparedStatement.setString(1, accessLogDto.getDate());
            preparedStatement.setString(2, accessLogDto.getIp());
            preparedStatement.setString(3, accessLogDto.getRequest());
            preparedStatement.setInt(4, accessLogDto.getStatus());
            preparedStatement.setString(5, accessLogDto.getUserAgent());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {

            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        super.closeDbConnection();
    }
}
