package com.acguglielmo.accesslogmonitor.threshold;

import java.time.LocalDateTime;

import com.acguglielmo.accesslogmonitor.enums.Duration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Threshold {

	@Getter
	private final LocalDateTime startDate;
	
	private final Duration duration;
	
	@Getter
	private final int limit;
	
    public LocalDateTime getEndDate() {
        switch (duration) {
            case HOURLY:  return startDate.plusHours(1);
            case DAILY:   return startDate.plusDays(1);
            default:      return LocalDateTime.now();
        }
    }

}
