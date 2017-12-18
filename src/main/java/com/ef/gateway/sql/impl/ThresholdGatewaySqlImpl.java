package com.ef.gateway.sql.impl;

import com.ef.dto.ThresholdDto;
import com.ef.gateway.ThresholdGateway;
import com.ef.gateway.sql.SqlGateway;
import com.ef.util.DateFormatter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThresholdGatewaySqlImpl extends SqlGateway implements ThresholdGateway {

    @Override
    public List<ThresholdDto> find(Date start, Date end, String threshold) {
        final List<ThresholdDto> result = new ArrayList<>();

        final String selectSql = "SELECT ip, count(1) as cont " +
                "  FROM usr_log.access_log t " +
                " where t.date > ? " +
                "   and t.date < ? " +
                "group by ip having cont > ? " +
                "order by cont desc;";

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = getConnection().prepareStatement(selectSql);

            preparedStatement.setString(1, DateFormatter.DATE_FORMAT_FILE.format(start));
            preparedStatement.setString(2, DateFormatter.DATE_FORMAT_FILE.format(end));
            preparedStatement.setString(3, threshold);

            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                final ThresholdDto dto = new ThresholdDto();
                dto.setCount(resultSet.getInt("cont"));
                dto.setIp(resultSet.getString("ip"));
                dto.setStartDate(start);
                dto.setEndDate(end);
                result.add(dto);
            }

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
            return result;
        }
    }

    @Override
    public void insert(final ThresholdDto thresholdDto) {
        final String insertTableSQL = "INSERT INTO usr_log.threshold"
                + "(ip, start_date, end_date, comment) VALUES"
                + "(?,?,?,?)";

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = getConnection().prepareStatement(insertTableSQL);

            preparedStatement.setString(1, thresholdDto.getIp());
            preparedStatement.setString(2, DateFormatter.DATE_FORMAT_FILE.format(thresholdDto.getStartDate()));
            preparedStatement.setString(3, DateFormatter.DATE_FORMAT_FILE.format(thresholdDto.getEndDate()));
            preparedStatement.setString(4, "Blocked after " + thresholdDto.getCount() + " attempts.");

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
