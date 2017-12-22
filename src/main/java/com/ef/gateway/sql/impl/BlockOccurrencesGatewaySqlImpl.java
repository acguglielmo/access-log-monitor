package com.ef.gateway.sql.impl;

import com.ef.dto.BlockOccurrencesDto;
import com.ef.gateway.sql.DbConnectionWrapper;
import com.ef.util.DateUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * The type Block occurrences gateway sql.
 */
public class BlockOccurrencesGatewaySqlImpl {

    private static final int BATCH_CHUNK_SIZE = 1000;

    private DbConnectionWrapper dbConnectionWrapper;

    private PreparedStatement preparedStatement;

    /**
     * Instantiates a new Block occurrences gateway sql.
     */
    public BlockOccurrencesGatewaySqlImpl() {
        this.dbConnectionWrapper = new DbConnectionWrapper();
    }

    /**
     * Table exists.
     *
     * @throws SQLException           the sql exception
     */
    public void tableExists() throws SQLException {
        try {
            final String statement = "SELECT 1 FROM usr_aguglielmo.block_occurrences";
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
     * @param blockOccurrencesDtoList the block occurrences dto list
     * @throws SQLException           the sql exception
     */
    public void insert(final List<BlockOccurrencesDto> blockOccurrencesDtoList) throws SQLException {
        final String insertTableSQL = "REPLACE INTO usr_aguglielmo.block_occurrences"
                + "(ip, start_date, end_date, comment, threshold) VALUES"
                + "(?,?,?,?,?)";

        try {
            int count = 0;

            preparedStatement = dbConnectionWrapper.getConnection().prepareStatement(insertTableSQL);

            for (final BlockOccurrencesDto blockOccurrencesDto : blockOccurrencesDtoList) {
                preparedStatement.setString(1, blockOccurrencesDto.getIp());
                preparedStatement.setString(2, DateUtils.DATE_FORMAT_FILE.format(blockOccurrencesDto.getStartDate()));
                preparedStatement.setString(3, DateUtils.DATE_FORMAT_FILE.format(blockOccurrencesDto.getEndDate()));
                preparedStatement.setString(4, "Blocked after " + blockOccurrencesDto.getCount() + " requests.");
                preparedStatement.setInt(5, blockOccurrencesDto.getThreshold());
                preparedStatement.addBatch();

                if(++count % BATCH_CHUNK_SIZE == 0) {
                    preparedStatement.executeBatch();
                }
            }
            preparedStatement.executeBatch();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            dbConnectionWrapper.closeDbConnection();
        }
    }
}
