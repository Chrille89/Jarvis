package de.bach.thwildau.jarvis.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.bach.thwildau.jarvis.model.LogLevel;

public abstract class Logger {

	private String className;
	
	public Logger(String className){
		this.className = className;
	}
	
	public void log(LogLevel level, String message){
		
		Date date = new Date();
	    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.YYYY HH:MM");
		
	    String msg = this.className;
	    msg+=" "+formatter.format(date);
	    msg+=" "+level.name();
	    msg+=" "+message;
	    
		if(level == LogLevel.DEBUG){
			debug(message);
		} else {
			switch(level){
			case INFO: info(msg);
			 break;
			case WARN: warn(msg);
			 break;
			case ERROR: error(msg);
			 break;
			default:
				debug(message);
			}
		}
	}
	
	protected void debug(String message){
		System.out.println(message);
	}
	
	protected abstract void info(String message);
	protected abstract void warn(String message);
	protected abstract void error(String message);
	
	
	
}
