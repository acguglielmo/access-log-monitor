package com.ef.gateway;

import com.ef.dto.ThresholdDto;

import java.util.Date;
import java.util.List;

public interface ThresholdGateway {
    List<ThresholdDto> find(Date start, Date end, String threshold);
    void insert(List<ThresholdDto> thresholdDto);
}
