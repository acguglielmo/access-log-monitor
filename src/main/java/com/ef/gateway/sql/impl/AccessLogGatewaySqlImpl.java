package com.ef.gateway.sql.impl;

import com.ef.dto.BlockOccurrencesDto;
import com.ef.gateway.sql.ConnectionFactory;
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

    private static final String INSERT_STATEMENT = "INSERT IGNORE INTO usr_aguglielmo.access_log"
            + "(date, ip, request, status, user_agent) VALUES"
            + "(?,?,?,?,?)";

    private static final String FIND_BLOCK_OCCURRENCES_STATEMENT = "SELECT ip, count(1) " +
            "  FROM usr_aguglielmo.access_log t " +
            " where t.date between ? " + " and ? " +
            "group by ip having count(1) > ? " +
            "order by count(1) desc;";

    private static final String TABLE_VERIFICATION_STATEMENT = "SELECT 1 FROM usr_aguglielmo.access_log";

    /**
     * Table exists.
     *
     * @throws SQLException           the sql exception
     */
    public void tableExists() throws SQLException {
        try (final Connection dbConnection = ConnectionFactory.getInstance().getConnection()) {
            try(final PreparedStatement preparedStatement = dbConnection.prepareStatement(TABLE_VERIFICATION_STATEMENT)) {
                preparedStatement.executeQuery();
            }
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

            try (Connection connection = ConnectionFactory.getInstance().getConnection()) {

                try (final PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STATEMENT)) {

                    for (final String data[] : dataList) {
                        preparedStatement.setString(1, data[0]);
                        preparedStatement.setString(2, data[1]);
                        preparedStatement.setString(3, data[2]);
                        preparedStatement.setString(4, data[3]);
                        preparedStatement.setString(5, data[4]);
                        preparedStatement.addBatch();
                    }
                    preparedStatement.executeBatch();
                }
            } finally {
                ApplicationStatus.getInstance().updateProgressByChunk();
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

        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {

            try (final PreparedStatement preparedStatement = connection.prepareStatement(FIND_BLOCK_OCCURRENCES_STATEMENT)) {

                preparedStatement.setString(1, DateUtils.DATE_FORMAT_FILE.format(start));
                preparedStatement.setString(2, DateUtils.DATE_FORMAT_FILE.format(end));
                preparedStatement.setInt(3, threshold);

                final ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    final BlockOccurrencesDto dto = new BlockOccurrencesDto();
                    dto.setIp(resultSet.getString(1));
                    dto.setCount(resultSet.getInt(2));
                    dto.setStartDate(start);
                    dto.setEndDate(end);
                    dto.setThreshold(threshold);
                    result.add(dto);
                }
            }
        }
        return result;
    }
}
