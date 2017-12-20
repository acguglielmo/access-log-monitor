package com.ef.gateway.sql.impl;

import com.ef.dto.BlockOccurrencesDto;
import com.ef.gateway.sql.SqlGateway;
import com.ef.util.DateUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccessLogGatewaySqlImpl extends SqlGateway {

    public AccessLogGatewaySqlImpl() {
        super();
    }

    @Override
    public boolean tableExists() throws SQLException {
        try {
            final String statement = "SELECT 1 FROM usr_aguglielmo.access_log";
            preparedStatement = getConnection().prepareStatement(statement);
            preparedStatement.executeQuery();
        } finally {
            super.closeDbConnection();
        }
        return true;
    }

    public void insert(final List<String[]> dataList) throws SQLException {

        try {
            final String statement = "INSERT INTO usr_aguglielmo.access_log"
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
        } finally {
            super.closeDbConnection();
        }
    }

    public List<BlockOccurrencesDto> find(final LocalDateTime start,
                                          final LocalDateTime end, final Integer threshold) throws SQLException {
        final List<BlockOccurrencesDto> result = new ArrayList<>();

        final String selectSql = "SELECT ip, count(1) as cont " +
                "  FROM usr_aguglielmo.access_log t " +
                " where t.date between ? " + " and ? " +
                "group by ip having cont > ? " +
                "order by cont desc;";

        try {
            preparedStatement = getConnection().prepareStatement(selectSql);

            preparedStatement.setString(1, DateUtils.DATE_FORMAT_FILE.format(start));
            preparedStatement.setString(2, DateUtils.DATE_FORMAT_FILE.format(end));
            preparedStatement.setInt(3, threshold);

            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                final BlockOccurrencesDto dto = new BlockOccurrencesDto();
                dto.setCount(resultSet.getInt("cont"));
                dto.setIp(resultSet.getString("ip"));
                dto.setStartDate(start);
                dto.setEndDate(end);
                dto.setThreshold(threshold);
                result.add(dto);
            }
        } finally {
            super.closeDbConnection();
        }
        return result;
    }

    public void truncate() throws SQLException {
        try {
            final String statement = "TRUNCATE TABLE usr_aguglielmo.access_log";
            preparedStatement = getConnection().prepareStatement(statement);
            preparedStatement.executeUpdate();
        } finally {
            super.closeDbConnection();
        }
    }
}
