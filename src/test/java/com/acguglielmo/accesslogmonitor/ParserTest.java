package com.acguglielmo.accesslogmonitor;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ParserTest {

	@Test
	public void shouldDoNothingWhenNoArgsAreProvidedTest() {

		final String[] args = null;
		
		Parser.main(args);
		
		assertNull(args);
		
	}
	
}
