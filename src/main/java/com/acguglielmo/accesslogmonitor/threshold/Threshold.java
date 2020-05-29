package com.acguglielmo.accesslogmonitor.threshold;

import java.time.LocalDateTime;

import com.acguglielmo.accesslogmonitor.enums.Duration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Threshold {

	@Getter
	protected final LocalDateTime startDate;
	
	@Getter
	protected final int limit;

	public static Threshold of(final Duration duration, final LocalDateTime startDate, final int limit) {

		return Duration.DAILY.equals(duration) ? 
			new DailyThreshold(startDate, limit) : new HourlyThreshold(startDate, limit);

	}
	
    public abstract LocalDateTime getEndDate();

}
