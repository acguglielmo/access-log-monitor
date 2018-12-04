package com.acguglielmo.accesslogmonitor.gateway.sql.impl;

import com.acguglielmo.accesslogmonitor.dto.BlockOccurrencesDto;
import com.acguglielmo.accesslogmonitor.gateway.sql.ConnectionFactory;
import com.acguglielmo.accesslogmonitor.util.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * The type Block occurrences gateway sql.
 */
public class BlockOccurrencesGatewaySqlImpl {

    private static final int BATCH_CHUNK_SIZE = 1000;

    private static final String INSERT_STATEMENT = "REPLACE INTO usr_aguglielmo.block_occurrences"
            + "(ip, start_date, end_date, comment, threshold) VALUES"
            + "(?,?,?,?,?)";

    private static final String TABLE_VERIFICATION_STATEMENT = "SELECT 1 FROM usr_aguglielmo.block_occurrences";

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
     * @param blockOccurrencesDtoList the block occurrences dto list
     * @throws SQLException           the sql exception
     */
    public void insert(final List<BlockOccurrencesDto> blockOccurrencesDtoList) throws SQLException {
        int count = 0;

        try (Connection dbConnection = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement preparedStatement = dbConnection.prepareStatement(INSERT_STATEMENT)) {

                for (final BlockOccurrencesDto blockOccurrencesDto : blockOccurrencesDtoList) {
                    preparedStatement.setString(1, blockOccurrencesDto.getIp());
                    preparedStatement.setString(2, DateUtils.DATE_FORMAT_FILE.format(blockOccurrencesDto.getStartDate()));
                    preparedStatement.setString(3, DateUtils.DATE_FORMAT_FILE.format(blockOccurrencesDto.getEndDate()));
                    preparedStatement.setString(4, "Blocked after " + blockOccurrencesDto.getCount() + " requests.");
                    preparedStatement.setInt(5, blockOccurrencesDto.getThreshold());
                    preparedStatement.addBatch();

                    if (++count % BATCH_CHUNK_SIZE == 0) {
                        preparedStatement.executeBatch();
                    }
                }
                preparedStatement.executeBatch();
            }
        }
    }
}
