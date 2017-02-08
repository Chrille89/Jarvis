package de.bach.thwildau.jarvis.operations;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.glassfish.jersey.internal.util.ExceptionUtils;

import de.bach.thwildau.jarvis.logging.FileLogger;
import de.bach.thwildau.jarvis.model.LogLevel;
import javazoom.jlme.util.Player;

public class StreamOf90th implements Function {

	private static final String streamUrl = "http://mp3.ffh.de/ffhchannels/hq90er.mp3";
	private static StreamOf90th instance;
	private FileLogger logger;
	
	private StreamOf90th(String answer) {
		logger = FileLogger.getLogger(this.getClass().getSimpleName());
	}

	public static StreamOf90th getInstance(String answer) {
		if (instance == null) {
			instance = new StreamOf90th(answer);
			return instance;
		} else {
			return instance;
		}
	}
	
	@Override
	public String operate() {
		URL url = null;
		try {
			url = new URL(streamUrl);
			
			Player player = new Player(url.openStream());
			
			Thread musicThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						player.play();
					} catch (Exception e) {
						logger.log(LogLevel.WARN, "Fehler beim Abspielen des Neunziger-Streams! "+ExceptionUtils.exceptionStackTraceAsString(e));
					}
				}
			});
			
			musicThread.start();
			
			Thread.sleep(1800000);
			
			player.stop();

		} catch (MalformedURLException e) {
			logger.log(LogLevel.WARN, "Die Neunziger-Stream-Url ist falsch formatiert! "+ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (IOException e) {
			logger.log(LogLevel.WARN, "IO-Fehler! "+ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (Exception e) {
			logger.log(LogLevel.WARN, "Unbekannter Fehler! "+ExceptionUtils.exceptionStackTraceAsString(e));
		}
		return null;
	}
}
