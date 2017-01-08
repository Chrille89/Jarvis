package de.bach.thwildau.jarvis.logging.test;

import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.junit.Test;

import de.bach.thwildau.jarvis.logging.FileLogger;
import de.bach.thwildau.jarvis.model.LogLevel;

public class LoggerTest {

	@Test
	public void testErrorLogFile(){
		
		FileLogger logger = FileLogger.getLogger(LoggerTest.class.getSimpleName());
		logger.log(LogLevel.WARN, "sechster Test");
	}
}
