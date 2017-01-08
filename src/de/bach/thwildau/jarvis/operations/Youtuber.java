package de.bach.thwildau.jarvis.operations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.util.ExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bach.thwildau.jarvis.client.StartClient;
import de.bach.thwildau.jarvis.logging.FileLogger;
import de.bach.thwildau.jarvis.model.GoogleResponse;
import de.bach.thwildau.jarvis.model.LogLevel;

public class Youtuber implements Function {

	private static Youtuber instance;
	private StartClient client = null;
	private FileLogger logger;

	private Youtuber(StartClient client) {
		logger = FileLogger.getLogger(this.getClass().getSimpleName());
		this.client = client;
	}

	public static Youtuber getInstance(StartClient client) {
		if (instance == null) {
			instance = new Youtuber(client);
			return instance;
		} else {
			return instance;
		}
	}

	@Override
	public String operate() {
		boolean restartYoutube = true;
		client.writeAnswer("Was möchtest du sehen?");
		
		while(restartYoutube){
			String token = client.getGoogleToken();
			String audioCmd = client.recordingCommando();
			Response response = client.startGoogleRequest(token, audioCmd);

			if(response == null){
				try {
					logger.log(LogLevel.DEBUG, "Response was null -> try again after 10 seconds...");
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					logger.log(LogLevel.ERROR, "Cannot sleep! " + ExceptionUtils.exceptionStackTraceAsString(e));
				}
			}
			restartYoutube = startYoutube(response);
		}
		return "Youtuber wurde beendet!";
	}

	private boolean startYoutube(Response response) {
		String question = "";

		if (response.getStatus() == 200) {
			System.out.println("Google send Status 200 ;-)");
			GoogleResponse googleResponse = response.readEntity(GoogleResponse.class);

			ObjectMapper mapper = new ObjectMapper();
			try {
				String strResponse = mapper.writeValueAsString(googleResponse);
				System.out.println("Response: " + strResponse);
			} catch (JsonProcessingException e) {
				logger.log(LogLevel.WARN, "Error in Parsing JSON! "+ExceptionUtils.exceptionStackTraceAsString(e));
			}

			if (googleResponse.getResults().size() > 0) {
				question = googleResponse.getResults().get(0).getAlternatives().get(0).getTranscript();
				logger.log(LogLevel.DEBUG, "Question: "+question);
				if (question.equalsIgnoreCase("YouTube beenden")) {
					logger.log(LogLevel.DEBUG, "Exit Youtube!");
					return false;
				}

				String cmd = "";
				try {

					File questionFile = new File("youtube/song.json");

					if (questionFile.exists()) {
						questionFile.delete();
						questionFile.createNewFile();
					} else {
						questionFile.createNewFile();
					}

					FileWriter writer = new FileWriter(questionFile);
					BufferedWriter bufferedWriter = new BufferedWriter(writer);

					bufferedWriter.write(question);
					bufferedWriter.close();

					String file = question.replaceAll("\\s+", "");

					cmd = "sudo ./playYoutubeVideo.sh " + file;
					logger.log(LogLevel.DEBUG, "Execute Command " + cmd);
					Runtime.getRuntime().exec(cmd).waitFor();
					
				} catch (IOException e) {
					logger.log(LogLevel.WARN, "Error execute commando '" + cmd + "' "+ExceptionUtils.exceptionStackTraceAsString(e));
				} catch (InterruptedException e) {
					logger.log(LogLevel.WARN, "Error execute commando '" + cmd + "' "+ExceptionUtils.exceptionStackTraceAsString(e));
				}
			}
			return true;
		} else {
			String failureMessage = "Ich konnte keine Verbindung zu Google aufnehmen! Die Gegenstelle meldet den Status-Code: "+response.getStatus();
			logger.log(LogLevel.ERROR, failureMessage);
			client.writeAnswer(failureMessage);
			return true;	
		}
	}
}
