package com.ef.dto;

import java.time.LocalDateTime;

public class BlockOccurrencesDto {

    private String ip;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer count;
    private Integer threshold;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold ) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return String.format("Ip: %-15s - Count: %s", ip, count);
    }
}
