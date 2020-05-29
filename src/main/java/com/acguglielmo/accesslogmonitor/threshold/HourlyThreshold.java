package com.acguglielmo.accesslogmonitor.threshold;

import java.time.LocalDateTime;

public class HourlyThreshold extends Threshold {

	public HourlyThreshold(LocalDateTime startDate, int limit) {

		super(startDate, limit);

	}

	@Override
	public LocalDateTime getEndDate() {

		return startDate.plusHours(1);

	}

}
