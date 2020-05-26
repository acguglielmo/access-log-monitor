package com.acguglielmo.accesslogmonitor.exception;

import java.io.IOException;
import java.sql.SQLException;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.mysql.cj.jdbc.exceptions.MySQLTimeoutException;

public class ExceptionHandler {

    public static void printExceptionToConsole(final Exception e) {
        System.out.println();

        if (e.getCause() != null && e.getCause() instanceof RuntimeException) {
            final RuntimeException runtimeException = (RuntimeException) e.getCause();
            if (runtimeException.getCause() != null) {
                if (runtimeException.getCause() instanceof SQLException) {
                    printSQLException(runtimeException);
                } else if (runtimeException.getCause() instanceof IOException) {
                    printIOException(runtimeException);
                } else {
                    System.out.println(e.getMessage());
                }
                return;
            }
        }
        System.out.println(e.getMessage());
    }

    private static void printIOException(final RuntimeException runtimeException) {
        final IOException iOException = (IOException) runtimeException.getCause();
        System.out.println("An error occurred during a I/O operation: ");
        System.out.println(iOException.getMessage());
    }

    private static void printSQLException(final RuntimeException runtimeException) {
        final SQLException sqlException = (SQLException) runtimeException.getCause();
        System.out.println("An error occurred during a database operation: ");
        if (sqlException instanceof CommunicationsException || sqlException instanceof MySQLTimeoutException) {
            System.out.println("Please check if the configured database server is up and running.");
        } else {
            System.out.println(sqlException.getMessage());
        }
    }
	
}
