package com.ef.gateway.sql.impl;

import com.ef.dto.BlockOccurrencesDto;
import com.ef.gateway.sql.SqlGateway;
import com.ef.util.DateUtils;

import java.sql.SQLException;
import java.util.List;

public class BlockOccurrencesGatewaySqlImpl extends SqlGateway {

    private static final int BATCH_CHUNK_SIZE = 1000;

    @Override
    public boolean tableExists() throws SQLException {
        try {
            final String statement = "SELECT 1 FROM usr_aguglielmo.block_occurrences";
            preparedStatement = getConnection().prepareStatement(statement);
            preparedStatement.executeQuery();
        } finally {
            super.closeDbConnection();
        }
        return true;
    }

    public void insert(final List<BlockOccurrencesDto> blockOccurrencesDtoList) throws SQLException {
        final String insertTableSQL = "INSERT INTO usr_aguglielmo.block_occurrences"
                + "(ip, start_date, end_date, comment, threshold) VALUES"
                + "(?,?,?,?,?)";

        try {
            int count = 0;

            preparedStatement = getConnection().prepareStatement(insertTableSQL);

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
            super.closeDbConnection();
        }
    }
}
