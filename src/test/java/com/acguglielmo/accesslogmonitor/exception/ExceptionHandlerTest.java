package com.acguglielmo.accesslogmonitor.exception;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import com.acguglielmo.accesslogmonitor.ConsoleWatcherSupport;

public class ExceptionHandlerTest extends ConsoleWatcherSupport{

	@Test
	public void shouldPrintExceptionMessageToConsoleTest() throws Exception {
		
		ExceptionHandler.printExceptionToConsole(new RuntimeException("hello"));
		
		assertEquals("\r\nhello\r\n", outContent.toString());
		
	}
	
	@Test
	public void shouldPrintExceptionMessageToConsoleIfCauseDoesNotHaveCauseTest() throws Exception {
		
		final RuntimeException outer = new RuntimeException("outer", new RuntimeException("cause") );
		
		ExceptionHandler.printExceptionToConsole(outer);
		
		assertEquals("\r\nouter\r\n", outContent.toString());
		
	}
	
	@Test
	public void shouldPrintExceptionMessageToConsoleIfCauseHaveCauseThatIsNotSQLExceptionOrIOExceptionTest() throws Exception {
		
		final RuntimeException outer = new RuntimeException("outer", new RuntimeException("inner", new RuntimeException("cause")) );
		
		ExceptionHandler.printExceptionToConsole(outer);
		
		assertEquals("\r\nouter\r\n", outContent.toString());
		
	}

	@Test
	public void shouldPrintIOExceptionMessageToConsoleIfCauseHaveCauseThatIsIOExceptionTest() throws Exception {
		
		final RuntimeException outer = new RuntimeException("outer", new RuntimeException("inner", new IOException("ioException")) );
		
		ExceptionHandler.printExceptionToConsole(outer);
		
		assertEquals("\r\nAn error occurred during a I/O operation: \r\nioException\r\n", outContent.toString());
		
	}

	@Test
	public void shouldPrintSQLExceptionMessageToConsoleIfCauseHaveCauseThatIsSQLExceptionTest() throws Exception {
		
		final RuntimeException outer = new RuntimeException("outer", new RuntimeException("inner", new SQLException("sqlException")) );
		
		ExceptionHandler.printExceptionToConsole(outer);
		
		assertEquals("\r\nAn error occurred during a database operation: \r\nsqlException\r\n", outContent.toString());
		
	}
}
