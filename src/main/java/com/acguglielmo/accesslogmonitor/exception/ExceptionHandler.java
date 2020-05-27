package com.acguglielmo.accesslogmonitor.exception;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.mysql.cj.jdbc.exceptions.MySQLTimeoutException;

public class ExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(ExceptionHandler.class);
	
    public static void printExceptionToConsole(final Exception e) {

        if (e.getCause() != null && e.getCause() instanceof RuntimeException) {
            final RuntimeException runtimeException = (RuntimeException) e.getCause();
            if (runtimeException.getCause() != null) {
                if (runtimeException.getCause() instanceof SQLException) {
                    printSQLException(runtimeException);
                } else if (runtimeException.getCause() instanceof IOException) {
                    printIOException(runtimeException);
                } else {
                	LOGGER.error(e.getMessage());
                }
                return;
            }
        }
        LOGGER.error(e.getMessage());
    }

    private static void printIOException(final RuntimeException runtimeException) {
        final IOException iOException = (IOException) runtimeException.getCause();
        LOGGER.error("An error occurred during a I/O operation: ");
        LOGGER.error(iOException.getMessage());
    }

    private static void printSQLException(final RuntimeException runtimeException) {
        final SQLException sqlException = (SQLException) runtimeException.getCause();
        LOGGER.error("An error occurred during a database operation: ");
        if (sqlException instanceof CommunicationsException || sqlException instanceof MySQLTimeoutException) {
        	LOGGER.error("Please check if the configured database server is up and running.");
        } else {
        	LOGGER.error(sqlException.getMessage());
        }
    }
	
}
