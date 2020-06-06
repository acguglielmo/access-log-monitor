package com.acguglielmo.accesslogmonitor;

import java.io.CharArrayWriter;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ConsoleWatcherSupport implements BeforeEachCallback, AfterEachCallback {
	
	private static final String PATTERN = "%msg%n";
	
	private static final String APPENDER_NAME = "log4jRuleAppender";
	
	private Logger logger;
	
	private Appender appender;

	private final CharArrayWriter outContent = new CharArrayWriter();

    public ConsoleWatcherSupport(final org.apache.logging.log4j.Logger logger2) {
        this.logger = ( org.apache.logging.log4j.core.Logger) logger2;
    }
	
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        StringLayout layout = PatternLayout.newBuilder().withPattern(PATTERN).build();
        appender = WriterAppender.newBuilder()
                .setTarget(outContent)
                .setLayout(layout)
                .setName(APPENDER_NAME).build();
        appender.start();
        logger.addAppender(appender);
    }
 
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        logger.removeAppender(appender);
    }
 
    public String getOutput() {
        return outContent.toString();
    }

}
