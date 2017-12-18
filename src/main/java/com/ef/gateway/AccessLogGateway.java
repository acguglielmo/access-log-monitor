package com.ef.gateway;

import com.ef.dto.AccessLogDto;
import com.ef.dto.ThresholdDto;

import java.util.Date;
import java.util.List;

public interface AccessLogGateway {
    void insert(AccessLogDto accessLogDto);
    void close();
}
