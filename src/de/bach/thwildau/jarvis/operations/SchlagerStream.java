package de.bach.thwildau.jarvis.operations;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.glassfish.jersey.internal.util.ExceptionUtils;

import de.bach.thwildau.jarvis.logging.FileLogger;
import de.bach.thwildau.jarvis.model.LogLevel;
import javazoom.jlme.util.Player;

public class SchlagerStream implements Function {

	private static final String schlagerStreamUrl = "http://mp3.ffh.de/ffhchannels/hqschlager.mp3";;
	private static SchlagerStream instance;
	private FileLogger logger;
	
	private SchlagerStream(String answer) {
		logger = FileLogger.getLogger(this.getClass().getSimpleName());
	}

	public static SchlagerStream getInstance(String answer) {
		if (instance == null) {
			instance = new SchlagerStream(answer);
			return instance;
		} else {
			return instance;
		}
	}
	
	@Override
	public String operate() {
		URL url = null;
		try {
			url = new URL(schlagerStreamUrl);
			
			Player player = new Player(url.openStream());
			
			Thread musicThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						player.play();
					} catch (Exception e) {
						logger.log(LogLevel.WARN, "Fehler beim Abspielen des Schlager-Streams! "+ExceptionUtils.exceptionStackTraceAsString(e));
					}
				}
			});
			
			musicThread.start();
			
			Thread.sleep(1800000);
			
			player.stop();

		} catch (MalformedURLException e) {
			logger.log(LogLevel.WARN, "Die Schlager-Stream-Url ist falsch formatiert! "+ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (IOException e) {
			logger.log(LogLevel.WARN, "IO-Fehler! "+ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (Exception e) {
			logger.log(LogLevel.WARN, "Unbekannter Fehler! "+ExceptionUtils.exceptionStackTraceAsString(e));
		}
		return null;
	}

}
