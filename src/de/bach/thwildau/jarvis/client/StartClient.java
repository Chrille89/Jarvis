package de.bach.thwildau.jarvis.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bach.thwildau.jarvis.client.model.Audio;
import de.bach.thwildau.jarvis.client.model.Config;
import de.bach.thwildau.jarvis.client.model.GoogleRequest;
import de.bach.thwildau.jarvis.model.GoogleResponse;
import de.bach.thwildau.jarvis.operations.BerlinTraffic;
import de.bach.thwildau.jarvis.operations.DateToday;
import de.bach.thwildau.jarvis.operations.Function;
import de.bach.thwildau.jarvis.operations.GameStarNews;
import de.bach.thwildau.jarvis.operations.GameStarVideos;
import de.bach.thwildau.jarvis.operations.GermanyTraffic;
import de.bach.thwildau.jarvis.operations.JobTicker;
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

	/**
	 * Constructor to initialize the Commandos
	 */
	public StartClient(Map<String, Function> commandos, Properties prop) {
		this.operations = commandos;
		this.prop = prop;
	}

	public StartClient() {
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
		while (true) {
			// playRecordingSound();

			if (index > 100) {
				try {
					// warte 16 min.
					System.out.println("Warte 16 Minuten...");
					writeAnswer("Da du mich im Moment nicht brauchst, schalte ich jetzt für 15 Minuten ab!");
					writeLog("DEBUG: ", "Warte 16 Minuten...");
					Thread.sleep(1000000);
					index = 0;
					token = getGoogleToken();
				} catch (InterruptedException e) {
					writeLog("ERROR: ", "GoogleToken konnte nicht erneuert werden!" + e.getMessage());
					e.printStackTrace();
				}
			}

			String audioCmd = recordingCommando();
			Response response = startGoogleRequest(token, audioCmd);

			while (response == null) {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				index = 0;
				audioCmd = recordingCommando();
				token = getGoogleToken();
				response = startGoogleRequest(token, audioCmd);
			}

			String answer = handleRequest(response);
			System.out.println(answer);
			this.writeAnswer(answer);
			index++;
		}
	}

	private void playRecordingSound() {
		String cmd = "sudo ./playRecordingSound.sh";

		try {
			Runtime.getRuntime().exec(cmd).waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Get a new Google-Token
	 */
	public String getGoogleToken() {
		System.out.println("Load Token...");
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
			writeLog("ERROR: ", "Error reading Token!" + e.getMessage());
			System.err.println("Error reading Token!");
			e.printStackTrace();
		}
		System.out.println("Done.");

		return token;
	}

	public void writeLog(String level, String message) {

		File errorLogFile = new File("logs/error.log");
		Date date = new Date();

		try {

			if (!errorLogFile.exists()) {
				errorLogFile.createNewFile();
			}
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(errorLogFile), "UTF-8");
			BufferedWriter buffWriter = new BufferedWriter(writer);

			buffWriter.write(date.toString() + " " + level + " " + message);
			buffWriter.close();
			writer.close();
		} catch (IOException e) {
			System.err.println("Das Log-File konnte nicht geschrieben werden!");
			e.printStackTrace();
		}

	}

	/*
	 * Recording the Commando.
	 */
	public String recordingCommando() {
		String cmd = "sudo ./record.sh";
		try {
			System.out.println("Recording...");
			Runtime.getRuntime().exec(cmd).waitFor();
		} catch (IOException e) {
			System.err.println("Error execute commando '" + cmd + "'");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("Error execute commando '" + cmd + "'");
			e.printStackTrace();
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
			System.out.println("Done.");
			return recordedStr;
		} catch (FileNotFoundException e1) {
			System.err.println("Error reading Audio-File!");
			e1.printStackTrace();
		}
		return recordedStr;
	}

	/*
	 * Start Google-Request
	 */
	public Response startGoogleRequest(String token, String audioString) {
		// Request erzeugen und an das Backend senden
		System.out.println("Create Request to Google...");
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
			System.err.println("Error parsing GoogleRequest-Class to JSON-Format!");
			e.printStackTrace();
		}

		try {
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON)
					.header("Content-Type", "application/json").header("Authorization", "Bearer " + token);

			response = invocationBuilder.post(Entity.entity(json, MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			writeLog("ERROR: ", "Error in Request " + index + ". Try again..." + e.getMessage());
			System.err.println("Error in Request " + index + ". Try again...");
			return null;
		}

		return response;
	}

	/*
	 * Handle the Request. If the token is invalid, renew the token and start
	 * the googleRequest again!
	 */
	private String handleRequest(Response response) {
		System.out.println("Request number: " + index);
		String answer = "";
		// Se Response.Status.OK;
		if (response.getStatus() == 200) {
			System.out.println("Google send Status 200 ;-) ");
			GoogleResponse googleResponse = response.readEntity(GoogleResponse.class);

			if (googleResponse.getResults().size() == 1) {
				String question = googleResponse.getResults().get(0).getAlternatives().get(0).getTranscript();
				System.out.println("Question: " + question);

				// Sprachsteuerung beenden
				if (question.equals("Sprachsteuerung deaktivieren")) {
					this.writeAnswer("Bis zum nächsten mal Christian! Ich hoffe wir reden später wieder?");
					System.exit(0);
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
			System.out.println("Google send: " + response.getStatus() + " :-(");
			System.out.println(response);
			writeLog("INFO: ", "Es ist ein Fehler in der Abarbeitung der " + index
					+ ".HTTP-Anfrage aufgetreten! Die Gegenstelle meldet den Fehler-Code: " + response.getStatus());
			writeAnswer("Es ist ein Fehler in der Abarbeitung der " + index
					+ ".HTTP-Anfrage aufgetreten! Die Gegenstelle meldet den Fehler-Code: " + response.getStatus());
			String newToken = getGoogleToken();
			String strAudio = recordingCommando();
			handleRequest(startGoogleRequest(newToken, strAudio));
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
			System.err.println("Error execute commando '" + cmd + "'");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("Error execute commando '" + cmd + "'");
			e.printStackTrace();
		}

	}

	/**
	 * Main-Method
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			InputStream stream = null;
			stream = StartClient.class.getClassLoader().getResourceAsStream("string.properties");
			
			Properties prop = new Properties();
			prop.load(stream);
			
			Map<String,Function> operations = new HashMap<>();
			
			// Time
			for(int i=1;i<3;i++){
				operations.put(prop.getProperty("question.time"+String.valueOf(i)), Time.getInstance(prop.getProperty("answer.time")));
			}
			
			// Date
			for(int i=1;i<4;i++){
				operations.put(prop.getProperty("question.date"+String.valueOf(i)), DateToday.getInstance(prop.getProperty("answer.date")));
			}
			
			// Gamestar-News
			for(int i=1; i < 6 ; i++){
				operations.put(prop.getProperty("question.gamestarnews"+String.valueOf(i)),
						GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));
			}
			
			// Gamestar-Videos
			for(int i=1; i<3;i++){
				operations.put(prop.getProperty("question.gamestarvideos"+String.valueOf(i)),
						GameStarVideos.getInstance(prop.getProperty("answer.gamestarvideos")));
			}
	
			// Youtube
			for(int i=1; i<6;i++){
				operations.put(prop.getProperty("question.youtube"+String.valueOf(i)),
						Youtuber.getInstance(new StartClient()));
			}
			
			// Wikipedia
			for(int i=1; i < 5 ; i++){
			operations.put(prop.getProperty("question.wikipedia"+String.valueOf(i)),
					WikipediaTracer.getInstance(new StartClient()));
			}
			
			// Tagesschau
			for (int i = 1; i < 22; i++) {
				operations.put(prop.getProperty("question.tagesschaunews" + String.valueOf(i)),
						TagesschauNews.getInstance(prop.getProperty("answer.tagesschau")));
			}
			
			// JobTicker
			for (int i = 1; i < 11; i++) {
					operations.put(prop.getProperty("question.job" + String.valueOf(i)),
					JobTicker.getInstance(prop.getProperty("answer.job")));
			}
			
			// Verkehrsmeldungen Deutschland
			for(int i=1; i<4; i++){
				operations.put(prop.getProperty("question.verkehr.deutschland" + String.valueOf(i)),
						GermanyTraffic.getInstance(prop.getProperty("answer.verkehr.deutschland")));
			}
			
			// Verkehrsmeldungen Berlin
			for(int i=1; i<4; i++){
				operations.put(prop.getProperty("question.verkehr.berlin" + String.valueOf(i)),
						BerlinTraffic.getInstance(prop.getProperty("answer.verkehr.berlin")));
			}
			

			// Wetter in Berlin
			for(int i=1; i<2; i++){
				operations.put(prop.getProperty("question.wetter.berlin" + String.valueOf(i)),
						WeatherForecastBerlin.getInstance(prop.getProperty("answer.wetter.berlin")));
			}
		
		
			StartClient client = new StartClient(operations,prop);
			client.start();
		} catch (IOException e) {
			System.err.println("Error reading Properties!");
			e.printStackTrace();
		}
		
	
	}

}
