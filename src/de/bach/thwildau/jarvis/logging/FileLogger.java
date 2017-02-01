package de.bach.thwildau.jarvis.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileLogger extends Logger {

	private static FileLogger uniqueInstance;

	private FileLogger(String className) {
		super(className);
	}

	public static FileLogger getLogger(String className) {
		if (uniqueInstance == null) {
			uniqueInstance = new FileLogger(className);
			return uniqueInstance;
		}
		return uniqueInstance;

	}

	private void writeLog(String message) {
		String errorLogFileName = "logs/error.log";

		try {
			message += "\n";
			Files.write(Paths.get(errorLogFileName), message.getBytes(), StandardOpenOption.APPEND);

		} catch (IOException e) {
			System.err.println("Das Log-File konnte nicht geschrieben werden!");
			e.printStackTrace();
		}
	}

	@Override
	protected void info(String message) {
		writeLog(message);

	}

	@Override
	protected void warn(String message) {
		writeLog(message);

	}

	@Override
	protected void error(String message) {
		writeLog(message);

	}

}
