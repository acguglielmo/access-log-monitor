package com.acguglielmo.accesslogmonitor.cli;

import org.apache.commons.cli.CommandLine;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApplicationCommandLine extends CommandLine {

	private final CommandLine commandLine;

	private static final long serialVersionUID = -5386208069947422875L;
	
	@Override
	public String getOptionValue(final String option) {
		
		return commandLine.getOptionValue(option);
		
	}

}
