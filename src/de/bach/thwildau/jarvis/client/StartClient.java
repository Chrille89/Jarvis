package de.bach.thwildau.jarvis.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.util.ExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bach.thwildau.jarvis.client.model.Audio;
import de.bach.thwildau.jarvis.client.model.Config;
import de.bach.thwildau.jarvis.client.model.GoogleRequest;
import de.bach.thwildau.jarvis.logging.FileLogger;
import de.bach.thwildau.jarvis.model.GoogleResponse;
import de.bach.thwildau.jarvis.model.LogLevel;
import de.bach.thwildau.jarvis.operations.BerlinTraffic;
import de.bach.thwildau.jarvis.operations.DateToday;
import de.bach.thwildau.jarvis.operations.Function;
import de.bach.thwildau.jarvis.operations.GameStarNews;
import de.bach.thwildau.jarvis.operations.GameStarVideos;
import de.bach.thwildau.jarvis.operations.GermanyTraffic;
import de.bach.thwildau.jarvis.operations.JobTicker;
import de.bach.thwildau.jarvis.operations.MailReader;
import de.bach.thwildau.jarvis.operations.RbbNews;
import de.bach.thwildau.jarvis.operations.TVSpielfilmPrimeTime;
import de.bach.thwildau.jarvis.operations.TagesschauNews;
import de.bach.thwildau.jarvis.operations.Time;
import de.bach.thwildau.jarvis.operations.WeatherForecastBerlin;
import de.bach.thwildau.jarvis.operations.WikipediaTracer;
import de.bach.thwildau.jarvis.operations.Youtuber;

/**
 * Main-Class
 * 
 * @author Christian
 *
 */
public class StartClient {

	private Map<String, Function> operations;
	private Properties prop;
	private int index = 0;
	private FileLogger logger;

	/**
	 * Constructor to initialize the Commandos
	 */
	public StartClient(Map<String, Function> commandos, Properties prop) {
		this.operations = commandos;
		this.prop = prop;
		logger = FileLogger.getLogger(this.getClass().getSimpleName());
	}

	public StartClient() {
		logger = FileLogger.getLogger(this.getClass().getSimpleName());
	}

	/**
	 * Start-Method:
	 * <ul>
	 * <li>Create the google-Token</li>
	 * <li>Recording the Commando</li>
	 * <li>Request to Google</li>
	 * <li>Handle the Request</li>
	 * <li>Write the answer to the TTS-Function of the Raspberrypi</li>
	 * </ul>
	 */
	public void start() {
		String token = getGoogleToken();
		this.writeAnswer("Jetzt können wir reden!");
		while (true) {
			// playRecordingSound();

			if (index > 100) {
				try {
					// warte 16 min.
					logger.log(LogLevel.DEBUG, "Wait 60 Minutes...");
					writeAnswer("Da du mich im Moment nicht brauchst, schalte ich jetzt für 60 Minuten ab!");
					Thread.sleep(3600000);
					index = 0;
					token = getGoogleToken();
				} catch (InterruptedException e) {
					logger.log(LogLevel.ERROR,
							"Cannot renew GoogleToken! " + ExceptionUtils.exceptionStackTraceAsString(e));
				}
			}

			String audioCmd = recordingCommando();
			Response response = startGoogleRequest(token, audioCmd);

			while (response == null) {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					logger.log(LogLevel.ERROR, "Cannot sleep! " + ExceptionUtils.exceptionStackTraceAsString(e));
				}
				index = 0;
				audioCmd = recordingCommando();
				token = getGoogleToken();
				response = startGoogleRequest(token, audioCmd);
			}

			String answer = handleRequest(response);
			logger.log(LogLevel.DEBUG, answer);
			this.writeAnswer(answer);
			index++;
		}
	}

	/*
	 * Get a new Google-Token
	 */
	public String getGoogleToken() {
		logger.log(LogLevel.DEBUG, "Load Token...");
		String token = "";
		// Token einlesen
		File file = new File("/home/pi/Jarvis/googleToken.json");

		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNext()) {
				token += scanner.next();
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			logger.log(LogLevel.ERROR, "Error reading Token! " + ExceptionUtils.exceptionStackTraceAsString(e));
		}
		logger.log(LogLevel.DEBUG, "Done.");
		return token;
	}

	/*
	 * Recording the Commando.
	 */
	public String recordingCommando() {
		String cmd = "sudo ./record.sh";
		try {
			logger.log(LogLevel.DEBUG, "Recording...");
			Runtime.getRuntime().exec(cmd).waitFor();
		} catch (IOException e) {
			logger.log(LogLevel.ERROR, "Error recording! " + ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (InterruptedException e) {
			logger.log(LogLevel.ERROR, "Error recording! " + ExceptionUtils.exceptionStackTraceAsString(e));
		}

		// Audio-File lesen
		File recordFile = new File("audio.json");

		String recordedStr = "";

		Scanner scanner;
		try {
			scanner = new Scanner(recordFile);

			while (scanner.hasNext()) {
				recordedStr += scanner.next();
			}
			scanner.close();
			logger.log(LogLevel.DEBUG, "Done.");
			return recordedStr;
		} catch (FileNotFoundException e) {
			logger.log(LogLevel.ERROR, "Cannot convert Audio-Command into String! The File "
					+ recordFile.getAbsolutePath() + " don't exist! " + ExceptionUtils.exceptionStackTraceAsString(e));
		}
		return recordedStr;
	}

	/*
	 * Start Google-Request
	 */
	public Response startGoogleRequest(String token, String audioString) {
		String msg = "Create Request to Google...";
		logger.log(LogLevel.DEBUG, msg);
		Response response = null;
		Audio audio = new Audio(audioString, null);
		Config config = new Config("FLAC", "16000", "de-DE", null);
		GoogleRequest googleRequest = new GoogleRequest(config, audio, null);

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target("https://speech.googleapis.com").path("/v1beta1/speech:syncrecognize");

		ObjectMapper mapper = new ObjectMapper();

		String json = null;
		try {
			json = mapper.writeValueAsString(googleRequest);
		} catch (JsonProcessingException e) {
			logger.log(LogLevel.WARN, "Error parsing GoogleRequest-Class to JSON-Format! "
					+ ExceptionUtils.exceptionStackTraceAsString(e));
			e.printStackTrace();
		}

		try {
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON)
					.header("Content-Type", "application/json").header("Authorization", "Bearer " + token);

			response = invocationBuilder.post(Entity.entity(json, MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			logger.log(LogLevel.WARN,
					"Error in Request " + index + ". Try again... " + ExceptionUtils.exceptionStackTraceAsString(e));
		}

		return response;
	}

	/*
	 * Handle the Request. If the token is invalid, renew the token and start
	 * the googleRequest again!
	 */
	private String handleRequest(Response response) {
		logger.log(LogLevel.DEBUG, "Request number: " + index);
		String answer = "";
		String errorMsg = "";
		// Se Response.Status.OK;
		if (response.getStatus() == 200) {
			logger.log(LogLevel.DEBUG, "Google send Status 200 ;-)");
			GoogleResponse googleResponse = response.readEntity(GoogleResponse.class);

			if (googleResponse.getResults().size() == 1) {
				String question = googleResponse.getResults().get(0).getAlternatives().get(0).getTranscript();
				logger.log(LogLevel.DEBUG, "Question: " + question);

				// Sprachsteuerung beenden
				if (question.equals("Sprachsteuerung deaktivieren")) {
					this.writeAnswer("Bis zum nächsten mal Christian! Ich hoffe wir reden später wieder?");
					System.exit(0);
				}

				// Herunterfahren
				if (question.equals("herunterfahren")) {
					this.writeAnswer("Bis zum nächsten mal Christian! Ich hoffe wir reden später wieder?");
					try {
						Runtime.getRuntime().exec("./shutdown.sh").waitFor();
						System.exit(0);
					} catch (IOException e) {
						logger.log(LogLevel.ERROR, "Cannot shutdown! " + ExceptionUtils.exceptionStackTraceAsString(e));
					} catch (InterruptedException e) {
						logger.log(LogLevel.ERROR, "Cannot shutdown! " + ExceptionUtils.exceptionStackTraceAsString(e));
					}
				}

				if (question.equals("neu starten")) {
					this.writeAnswer("Ich starte jetzt neu!");
					try {
						Runtime.getRuntime().exec("./reboot.sh").waitFor();
					} catch (IOException e) {
						logger.log(LogLevel.ERROR, "Cannot shutdown! " + ExceptionUtils.exceptionStackTraceAsString(e));
					} catch (InterruptedException e) {
						logger.log(LogLevel.ERROR, "Cannot shutdown! " + ExceptionUtils.exceptionStackTraceAsString(e));
					}
				}

				if (operations.get(question) != null) {
					Function function = operations.get(question);
					answer = function.operate();
					index = 0;
					return answer;
				} else {
					String error = prop.getProperty("question.notfound");
					return error;
				}
			}
		} else {
			// renew Token!
			logger.log(LogLevel.DEBUG, "Google send: " + response.getStatus() + " :-(");
			System.out.println(response);
			errorMsg = "Es ist ein Fehler in der Abarbeitung der " + index
					+ ".HTTP-Anfrage aufgetreten! Die Gegenstelle meldet den Fehler-Code: " + response.getStatus()
					+ "! Ich starte jetzt neu !";
			logger.log(LogLevel.WARN, errorMsg);
			writeAnswer(errorMsg);
			try {
				Runtime.getRuntime().exec("./reboot.sh");
			} catch (IOException e) {
				errorMsg = "Neustarten fehlgeschlagen!";
				logger.log(LogLevel.ERROR, errorMsg + " " + ExceptionUtils.exceptionStackTraceAsString(e));
				writeAnswer(errorMsg);
				e.printStackTrace();
			}
		}
		return answer;
	}

	/*
	 * Write the Answer in a File.
	 */
	public void writeAnswer(String answer) {
		String cmd = "sudo ./answer.sh";
		File file = new File("output.json");

		try {

			if (file.exists()) {
				file.delete();
			}

			if (file.createNewFile()) {
				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
				BufferedWriter buffWriter = new BufferedWriter(writer);
				buffWriter.write(answer);
				buffWriter.close();
			}
			Runtime.getRuntime().exec(cmd).waitFor();
		} catch (IOException e) {
			logger.log(LogLevel.ERROR,
					"Error execute commando '" + cmd + "' " + ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (InterruptedException e) {
			logger.log(LogLevel.ERROR,
					"Error execute commando '" + cmd + "' " + ExceptionUtils.exceptionStackTraceAsString(e));
		}

	}

	/**
	 * Main-Method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			InputStream stream = null;
			stream = StartClient.class.getClassLoader().getResourceAsStream("string.properties");

			Properties prop = new Properties();
			prop.load(stream);

			Map<String, Function> operations = new HashMap<>();

			// Time
			for (int i = 1; i < 3; i++) {
				operations.put(prop.getProperty("question.time" + String.valueOf(i)),
						Time.getInstance(prop.getProperty("answer.time")));
			}

			// Date
			for (int i = 1; i < 4; i++) {
				operations.put(prop.getProperty("question.date" + String.valueOf(i)),
						DateToday.getInstance(prop.getProperty("answer.date")));
			}

			// Gamestar-News
			for (int i = 1; i < 6; i++) {
				operations.put(prop.getProperty("question.gamestarnews" + String.valueOf(i)),
						GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));
			}

			// Gamestar-Videos
			for (int i = 1; i < 3; i++) {
				operations.put(prop.getProperty("question.gamestarvideos" + String.valueOf(i)),
						GameStarVideos.getInstance(prop.getProperty("answer.gamestarvideos")));
			}

			// Youtube
			for (int i = 1; i < 6; i++) {
				operations.put(prop.getProperty("question.youtube" + String.valueOf(i)),
						Youtuber.getInstance(new StartClient()));
			}

			// Wikipedia
			for (int i = 1; i < 5; i++) {
				operations.put(prop.getProperty("question.wikipedia" + String.valueOf(i)),
						WikipediaTracer.getInstance(new StartClient()));
			}

			// Tagesschau
			for (int i = 1; i < 22; i++) {
				operations.put(prop.getProperty("question.tagesschaunews" + String.valueOf(i)),
						TagesschauNews.getInstance(prop.getProperty("answer.tagesschau")));
			}

			// Nachrichten des RBB
			for (int i = 1; i < 9; i++) {
				operations.put(prop.getProperty("question.rbbnews" + String.valueOf(i)),
						RbbNews.getInstance(prop.getProperty("answer.rbb")));
			}

			// JobTicker
			for (int i = 1; i < 11; i++) {
				operations.put(prop.getProperty("question.job" + String.valueOf(i)),
						JobTicker.getInstance(prop.getProperty("answer.job")));
			}

			// Verkehrsmeldungen Deutschland
			for (int i = 1; i < 4; i++) {
				operations.put(prop.getProperty("question.verkehr.deutschland" + String.valueOf(i)),
						GermanyTraffic.getInstance(prop.getProperty("answer.verkehr.deutschland")));
			}

			// Verkehrsmeldungen Berlin
			for (int i = 1; i < 4; i++) {
				operations.put(prop.getProperty("question.verkehr.berlin" + String.valueOf(i)),
						BerlinTraffic.getInstance(prop.getProperty("answer.verkehr.berlin")));
			}

			// Wetter in Berlin
			for (int i = 1; i < 2; i++) {
				operations.put(prop.getProperty("question.wetter.berlin" + String.valueOf(i)),
						WeatherForecastBerlin.getInstance(prop.getProperty("answer.wetter.berlin")));
			}

			// Wetter in Berlin
			for (int i = 1; i < 2; i++) {
				operations.put(prop.getProperty("question.email" + String.valueOf(i)),
						MailReader.getInstance(prop.getProperty("answer.email")));
			}

			operations.put(prop.getProperty("question.tvspielfilm.primetime"),
					TVSpielfilmPrimeTime.getInstance(prop.getProperty("answer.tvspielfilm")));

			StartClient client = new StartClient(operations, prop);
			client.start();
		} catch (IOException e) {
			System.err.println("Error reading Properties!");
			e.printStackTrace();
		}

	}

}
