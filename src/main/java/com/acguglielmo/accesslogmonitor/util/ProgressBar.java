package com.acguglielmo.accesslogmonitor.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgressBar {

	private final double progress;
	
	public static ProgressBar of(final double progress) {
		
		return new ProgressBar(progress);
		
	}
	
	@Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        long x = (long) progress / 2;
        sb.append("|");
        for (int k = 0; k < 50; k++)
            sb.append((x <= k) ? " " : "=");
        sb.append("| ");
        sb.append((long) progress);
        sb.append("% Done");

        return sb.toString();
    }
	
}
