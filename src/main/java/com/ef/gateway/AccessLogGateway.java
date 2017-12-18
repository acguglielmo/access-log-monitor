package com.ef.gateway;

import java.util.List;

public interface AccessLogGateway {
    void insert(List<String[]> dataList);
    void open();
    void close();
}
