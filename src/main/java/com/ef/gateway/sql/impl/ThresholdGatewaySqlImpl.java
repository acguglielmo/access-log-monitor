package com.ef.gateway.sql.impl;

import com.ef.dto.BlockOccurrencesDto;
import com.ef.gateway.ThresholdGateway;
import com.ef.gateway.sql.SqlGateway;
import com.ef.util.DateUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ThresholdGatewaySqlImpl extends SqlGateway implements ThresholdGateway {

    private static final int BATCH_SIZE = 1000;

    @Override
    public List<BlockOccurrencesDto> find(final LocalDateTime start,
                                          final LocalDateTime end, final Integer threshold) {
        final List<BlockOccurrencesDto> result = new ArrayList<>();

        final String selectSql = "SELECT ip, count(1) as cont " +
                "  FROM usr_log.access_log t " +
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

        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            super.closeDbConnection();
        }
        return result;
    }

    @Override
    public void insert(final List<BlockOccurrencesDto> blockOccurrencesDtoList) {
        final String insertTableSQL = "INSERT INTO usr_log.block_occurrences"
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

                if(++count % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                }
            }
            preparedStatement.executeBatch();

        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            super.closeDbConnection();
        }
    }
}
