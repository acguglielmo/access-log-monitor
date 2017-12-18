package com.ef.gateway;

import com.ef.dto.BlockOccurrencesDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ThresholdGateway {
    List<BlockOccurrencesDto> find(LocalDateTime start, LocalDateTime end, Integer threshold);
    void insert(List<BlockOccurrencesDto> blockOccurrencesDtoList);
}
