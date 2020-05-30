package com.acguglielmo.accesslogmonitor.cli;

import static com.acguglielmo.accesslogmonitor.cli.CommandLineHelper.DURATION;
import static com.acguglielmo.accesslogmonitor.cli.CommandLineHelper.START_DATE;
import static com.acguglielmo.accesslogmonitor.cli.CommandLineHelper.THRESHOLD;

import java.time.LocalDateTime;

import org.apache.commons.cli.CommandLine;

import com.acguglielmo.accesslogmonitor.enums.Duration;
import com.acguglielmo.accesslogmonitor.util.DateUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApplicationCommandLine extends CommandLine {

	private final CommandLine commandLine;

	private static final long serialVersionUID = -5386208069947422875L;
	
	@Override
	public String getOptionValue(final String option) {
		
		return commandLine.getOptionValue(option);
		
	}
	
	public LocalDateTime getStartDate() {
		
		final String startDate = commandLine.getOptionValue(START_DATE);
		
		return LocalDateTime.parse(startDate, DateUtils.DATE_FORMAT_ARGS);
		
	}
	
	public Duration getDuration() {
		
		final String duration = commandLine.getOptionValue(DURATION);
		
		return Duration.getByName(duration);
		
	}

	public int getLimit() {
		
		final String limit = commandLine.getOptionValue(THRESHOLD);
		
		return Integer.parseInt(limit);
		
	}

}
