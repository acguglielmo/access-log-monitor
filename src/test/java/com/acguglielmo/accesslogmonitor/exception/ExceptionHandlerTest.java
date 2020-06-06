package com.acguglielmo.accesslogmonitor.exception;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.acguglielmo.accesslogmonitor.ConsoleWatcherSupport;

public class ExceptionHandlerTest {

	@RegisterExtension 
    public ConsoleWatcherSupport appender = new ConsoleWatcherSupport(LogManager.getLogger(ExceptionHandler.class));
	
	@Test
	public void shouldPrintExceptionMessageToConsoleTest() throws Exception {
		
		ExceptionHandler.printExceptionToConsole(new RuntimeException("hello"));
		
		assertEquals(format("hello%n"), appender.getOutput());
		
	}
	
	@Test
	public void shouldPrintExceptionMessageToConsoleIfCauseDoesNotHaveCauseTest() throws Exception {
		
		final RuntimeException outer = new RuntimeException("outer", new RuntimeException("cause") );
		
		ExceptionHandler.printExceptionToConsole(outer);
		
		assertEquals(format("outer%n"), appender.getOutput());
		
	}
	
	@Test
	public void shouldPrintExceptionMessageToConsoleIfCauseHaveCauseThatIsNotSQLExceptionOrIOExceptionTest() throws Exception {
		
		final RuntimeException outer = new RuntimeException("outer", new RuntimeException("inner", new RuntimeException("cause")) );
		
		ExceptionHandler.printExceptionToConsole(outer);
		
		assertEquals(format("outer%n"), appender.getOutput());
		
	}

	@Test
	public void shouldPrintIOExceptionMessageToConsoleIfCauseHaveCauseThatIsIOExceptionTest() throws Exception {
		
		final RuntimeException outer = new RuntimeException("outer", new RuntimeException("inner", new IOException("ioException")) );
		
		ExceptionHandler.printExceptionToConsole(outer);
		
		assertEquals(format("An error occurred during a I/O operation: %nioException%n"), appender.getOutput());
		
	}

	@Test
	public void shouldPrintSQLExceptionMessageToConsoleIfCauseHaveCauseThatIsSQLExceptionTest() throws Exception {
		
		final RuntimeException outer = new RuntimeException("outer", new RuntimeException("inner", new SQLException("sqlException")) );
		
		ExceptionHandler.printExceptionToConsole(outer);
		
		assertEquals(format("An error occurred during a database operation: %nsqlException%n"), appender.getOutput());
		
	}
	
}