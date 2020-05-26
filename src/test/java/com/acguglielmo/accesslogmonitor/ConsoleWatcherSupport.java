package com.acguglielmo.accesslogmonitor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;

public class ConsoleWatcherSupport {

	private final PrintStream originalOut = System.out;

	private final PrintStream originalErr = System.err;

	protected final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@Before
	public void before() {
		
		System.setOut(new PrintStream(outContent));
		
		System.setErr(new PrintStream(errContent));
		
	}

	@After
	public void after() {
	
		System.setOut(originalOut);
	
	    System.setErr(originalErr);
	
	}

}
