package de.bach.thwildau.jarvis.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileLogger extends Logger {

	private static FileLogger uniqueInstance;
	
	public FileLogger(String className){
		super(className);
	}
	
	public static FileLogger getLogger(String className){
		if(uniqueInstance == null){
			uniqueInstance = new FileLogger(className);
			return uniqueInstance;
		}
		return uniqueInstance;
		
	}
	
	private void writeLog(String message) {
			File errorLogFile = new File("logs/error.log");

			try {

				if (!errorLogFile.exists()) {
					errorLogFile.createNewFile();
				}
				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(errorLogFile), "UTF-8");
				BufferedWriter buffWriter = new BufferedWriter(writer);

				buffWriter.write(message);
				buffWriter.close();
				writer.close();
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
