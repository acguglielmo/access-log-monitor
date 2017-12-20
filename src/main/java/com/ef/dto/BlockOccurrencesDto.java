package com.ef.dto;

import java.time.LocalDateTime;

/**
 * The type Block occurrences dto.
 */
public class BlockOccurrencesDto {

    private String ip;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer count;
    private Integer threshold;

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public LocalDateTime getStartDate() {
        return startDate;
    }

    /**
     * Sets start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * Sets end date.
     *
     * @param endDate the end date
     */
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Sets count.
     *
     * @param count the count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * Gets threshold.
     *
     * @return the threshold
     */
    public Integer getThreshold() {
        return threshold;
    }

    /**
     * Sets threshold.
     *
     * @param threshold the threshold
     */
    public void setThreshold(Integer threshold ) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return String.format("Ip: %-15s - Count: %s", ip, count);
    }
}
