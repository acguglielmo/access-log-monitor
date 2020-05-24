package com.acguglielmo.accesslogmonitor.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BlockOccurrencesDto {

    private String ip;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer count;
    private Integer threshold;

    @Override
    public String toString() {
        return String.format("%-15s   %s", ip, count);
    }
}
