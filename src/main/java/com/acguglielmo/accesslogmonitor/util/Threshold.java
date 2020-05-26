package com.acguglielmo.accesslogmonitor.util;

import java.time.LocalDateTime;

import com.acguglielmo.accesslogmonitor.enums.Duration;

import lombok.Getter;

public class Threshold {

	@Getter
	private LocalDateTime startDate;
	
	private Duration duration;
	
	@Getter
	private int limit;

	public Threshold(final String startDate, final String duration, final String limit) {
		this.startDate = LocalDateTime.parse(startDate, DateUtils.DATE_FORMAT_ARGS);
		this.duration = Duration.getByName(duration);
		this.limit = Integer.parseInt(limit);
	}
	
	
    public LocalDateTime getEndDate() {
        switch (duration) {
            case HOURLY:  return startDate.plusHours(1);
            case DAILY:   return startDate.plusDays(1);
            default:      return LocalDateTime.now();
        }
    }

}
