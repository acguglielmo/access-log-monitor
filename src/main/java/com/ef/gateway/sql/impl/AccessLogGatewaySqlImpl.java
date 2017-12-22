package com.ef.gateway.sql.impl;

import com.ef.dto.BlockOccurrencesDto;
import com.ef.gateway.sql.DbConnectionWrapper;
import com.ef.util.ApplicationStatus;
import com.ef.util.DateUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Access log gateway sql.
 */
public class AccessLogGatewaySqlImpl {

    private DbConnectionWrapper dbConnectionWrapper;

    private PreparedStatement preparedStatement;

    /**
     * Instantiates a new Access log gateway sql.
     */
    public AccessLogGatewaySqlImpl() {
        this.dbConnectionWrapper = new DbConnectionWrapper();
    }

    /**
     * Table exists.
     *
     * @throws SQLException           the sql exception
     */
    public void tableExists() throws SQLException {
        try {
            final String statement = "SELECT 1 FROM usr_aguglielmo.access_log";
            preparedStatement = dbConnectionWrapper.getConnection().prepareStatement(statement);
            preparedStatement.executeQuery();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            dbConnectionWrapper.closeDbConnection();
        }
    }

    /**
     * Insert.
     *
     * @param dataList the data list
     * @throws SQLException           the sql exception
     */
    public void insert(final List<String[]> dataList) throws SQLException {

        if (!dataList.isEmpty()) {
            try {
                final String statement = "INSERT IGNORE INTO usr_aguglielmo.access_log"
                        + "(date, ip, request, status, user_agent) VALUES"
                        + "(?,?,?,?,?)";
                preparedStatement = dbConnectionWrapper.getConnection().prepareStatement(statement);

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
                ApplicationStatus.getInstance().updateProgressByChunk();
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                dbConnectionWrapper.closeDbConnection();
            }
        }
    }

    /**
     * Find list.
     *
     * @param start     the start
     * @param end       the end
     * @param threshold the threshold
     * @return the list
     * @throws SQLException           the sql exception
     */
    public List<BlockOccurrencesDto> find(final LocalDateTime start,
                                          final LocalDateTime end, final Integer threshold) throws SQLException {
        final List<BlockOccurrencesDto> result = new ArrayList<>();

        final String selectSql = "SELECT ip, count(1) " +
                "  FROM usr_aguglielmo.access_log t " +
                " where t.date between ? " + " and ? " +
                "group by ip having count(1) > ? " +
                "order by count(1) desc;";

        try {
            preparedStatement = dbConnectionWrapper.getConnection().prepareStatement(selectSql);

            preparedStatement.setString(1, DateUtils.DATE_FORMAT_FILE.format(start));
            preparedStatement.setString(2, DateUtils.DATE_FORMAT_FILE.format(end));
            preparedStatement.setInt(3, threshold);

            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                final BlockOccurrencesDto dto = new BlockOccurrencesDto();
                dto.setCount(resultSet.getInt("c2"));
                dto.setIp(resultSet.getString("ip"));
                dto.setStartDate(start);
                dto.setEndDate(end);
                dto.setThreshold(threshold);
                result.add(dto);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            dbConnectionWrapper.closeDbConnection();
        }
        return result;
    }
}
