package com.acguglielmo.accesslogmonitor.threshold;

import java.time.LocalDateTime;

public class DailyThreshold extends Threshold {

	public DailyThreshold(final LocalDateTime startDate, final int limit) {

		super(startDate, limit);

	}

	@Override
	public LocalDateTime getEndDate() {

		return startDate.plusDays(1);
	
	}

}
