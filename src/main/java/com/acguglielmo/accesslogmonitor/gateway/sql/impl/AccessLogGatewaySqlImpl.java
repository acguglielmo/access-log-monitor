package com.acguglielmo.accesslogmonitor.gateway.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.gateway.sql.ConnectionFactory;
import com.acguglielmo.accesslogmonitor.util.ApplicationStatus;
import com.acguglielmo.accesslogmonitor.util.DateUtils;
import com.acguglielmo.accesslogmonitor.util.Threshold;

public class AccessLogGatewaySqlImpl {

    private static final String INSERT_STATEMENT = "INSERT IGNORE INTO access_log"
            + "(date, ip, request, status, user_agent) VALUES"
            + "(?,?,?,?,?)";

    private static final String FIND_BLOCK_OCCURRENCES_STATEMENT = "SELECT ip, count(1) " +
            "  FROM access_log t " +
            " where t.date between ? " + " and ? " +
            "group by ip having count(1) > ? " +
            "order by count(1) desc;";

    private static final String TABLE_VERIFICATION_STATEMENT = "SELECT 1 FROM access_log";

    public void tableExists() throws SQLException {
        try (final Connection dbConnection = ConnectionFactory.getInstance().getConnection()) {
            try(final PreparedStatement preparedStatement = dbConnection.prepareStatement(TABLE_VERIFICATION_STATEMENT)) {
                preparedStatement.executeQuery();
            }
        }
    }

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

    public List<BlockOccurrencesDto> find(final Threshold threshold) throws SQLException {
        
    	final List<BlockOccurrencesDto> result = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {

            try (final PreparedStatement preparedStatement = connection.prepareStatement(FIND_BLOCK_OCCURRENCES_STATEMENT)) {

                preparedStatement.setString(1, DateUtils.DATE_FORMAT_FILE.format(threshold.getStartDate()));
                preparedStatement.setString(2, DateUtils.DATE_FORMAT_FILE.format(threshold.getEndDate()));
                preparedStatement.setInt(3, threshold.getLimit());

                final ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    final BlockOccurrencesDto dto = BlockOccurrencesDto.builder()
	            		.ip(resultSet.getString(1))
	            		.count(resultSet.getInt(2))
	            		.startDate(threshold.getStartDate())
	            		.endDate(threshold.getEndDate())
	            		.threshold(threshold.getLimit())
	            		.build();
                    result.add(dto);
                }
            }
        }
        return result;
    }
}
