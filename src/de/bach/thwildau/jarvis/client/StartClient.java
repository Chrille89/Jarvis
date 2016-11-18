package de.bach.thwildau.jarvis.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bach.thwildau.jarvis.client.model.Audio;
import de.bach.thwildau.jarvis.client.model.Config;
import de.bach.thwildau.jarvis.client.model.GoogleRequest;
import de.bach.thwildau.jarvis.model.GoogleResponse;
import de.bach.thwildau.jarvis.operations.DateToday;
import de.bach.thwildau.jarvis.operations.Function;
import de.bach.thwildau.jarvis.operations.GameStarNews;
import de.bach.thwildau.jarvis.operations.GameStarVideos;
import de.bach.thwildau.jarvis.operations.TagesschauNews;
import de.bach.thwildau.jarvis.operations.Time;
import de.bach.thwildau.jarvis.operations.Youtuber;

/**
 * Main-Class
 * @author Christian
 *
 */
public class StartClient {

	private Map<String, Function> operations;
	private Properties prop;

	/**
	 * Constructor to initialize the Commandos
	 */
	public StartClient(Map<String,Function> commandos, Properties prop) {
		this.operations = commandos;
		this.prop = prop;
	}
	
	public StartClient(){}
	
	
	/**
	 * Start-Method:
	 * 		<ul>
	 *			<li>Create the google-Token</li>
	 *			<li>Recording the Commando</li>
	 *			<li>Request to Google</li>
	 *			<li>Handle the Request</li>
	 *			<li>Write the answer to the TTS-Function of the Raspberrypi</li>
	 *		</ul>
	 */
	public void start(){
		String token = renewGoogleToken();
		this.writeAnswer("Ich bin nun bereit fuer dich!");
		while(true){
		String audioCmd = recordingCommando();
		Response response = startGoogleRequest(token, audioCmd);
		String answer = handleRequest(response);
		writeAnswer(answer);
		}
	}
	

	/*
	 * Get a new Google-Token
	 */
	public String renewGoogleToken() {
		System.out.println("Generate Token...");
		String cmd = "sudo ./renewToken.sh";
		String token = "";
		try {
			Runtime.getRuntime().exec(cmd).waitFor();

			// Token einlesen
			File file = new File("/home/pi/Jarvis/googleToken.json");

			try {
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					token += scanner.next();
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				System.err.println("Error reading Token!");
				e.printStackTrace();
			}
			System.out.println("Done.");
		} catch (InterruptedException e) {
			System.err.println("Error execute commando '" + cmd + "'");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error execute commando '" + cmd + "'");
			e.printStackTrace();
		}

		return token;
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

		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON)
				.header("Content-Type", "application/json").header("Authorization", "Bearer " + token);

		Response response = invocationBuilder.post(Entity.entity(json, MediaType.APPLICATION_JSON));

		return response;
	}
	
	/*
	 * Handle the Request.
	 * If the token is invalid, renew the token and start the googleRequest again!
	 */
	private String handleRequest(Response response){
		String answer ="";
		// Se Response.Status.OK;
		if (response.getStatus() == 200) {
			System.out.println("Google send Status 200 ;-) ");
			GoogleResponse googleResponse = response.readEntity(GoogleResponse.class);

			if (googleResponse.getResults().size() == 1) {
				String question = googleResponse.getResults().get(0).getAlternatives().get(0).getTranscript();
				System.out.println("Question: "+question);
				if (operations.get(question) != null) {
					Function function = operations.get(question);
					answer = function.operate();
					return answer;
				} else {
					String error = prop.getProperty("question.notfound");
					return error;
				}
			}
		} else {
			// renew Token!
			String newToken = renewGoogleToken();
			String strAudio = recordingCommando();
			handleRequest(startGoogleRequest(newToken, strAudio));
		}
		return answer;

	}

	/*
	 * Write the Answer in a File.
	 */
	public void writeAnswer(String answer) {
		System.out.println("Answer: " + answer);
		String cmd = "sudo ./answer.sh";
		File file = new File("output.json");

		try {

			if (file.exists()) {
				file.delete();
			}

			if (file.createNewFile()) {
				FileWriter writer = new FileWriter(file.getAbsolutePath());
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
			
			operations.put(prop.getProperty("question.time1"), Time.getInstance(prop.getProperty("answer.time")));
			operations.put(prop.getProperty("question.time2"), Time.getInstance(prop.getProperty("answer.time")));
			operations.put(prop.getProperty("question.date1"), DateToday.getInstance(prop.getProperty("answer.date")));
			operations.put(prop.getProperty("question.date2"), DateToday.getInstance(prop.getProperty("answer.date")));
			operations.put(prop.getProperty("question.date3"), DateToday.getInstance(prop.getProperty("answer.date")));

			operations.put(prop.getProperty("question.gamestarnews1"),
					GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));
			operations.put(prop.getProperty("question.gamestarnews2"),
					GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));
			operations.put(prop.getProperty("question.gamestarnews3"),
					GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));
			operations.put(prop.getProperty("question.gamestarnews4"),
					GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));
			operations.put(prop.getProperty("question.gamestarnews5"),
					GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));

			operations.put(prop.getProperty("question.gamestarvideos1"),
					GameStarVideos.getInstance(prop.getProperty("answer.gamestarvideos")));
			operations.put(prop.getProperty("question.gamestarvideos2"),
					GameStarVideos.getInstance(prop.getProperty("answer.gamestarvideos")));
			operations.put(prop.getProperty("question.youtube1"),
					Youtuber.getInstance(new StartClient()));
			operations.put(prop.getProperty("question.youtube2"),
					Youtuber.getInstance(new StartClient()));
			operations.put(prop.getProperty("question.youtube3"),
					Youtuber.getInstance(new StartClient()));
			operations.put(prop.getProperty("question.youtube4"),
					Youtuber.getInstance(new StartClient()));
			operations.put(prop.getProperty("question.youtube5"),
					Youtuber.getInstance(new StartClient()));

			// Tagesschau
			for (int i = 1; i < 22; i++) {
				operations.put(prop.getProperty("question.tagesschaunews" + String.valueOf(i)),
						TagesschauNews.getInstance(prop.getProperty("answer.tagesschau")));
			}
			StartClient client = new StartClient(operations,prop);
			client.start();
		} catch (IOException e) {
			System.err.println("Error reading Properties!");
			e.printStackTrace();
		}
		
	
	}

}
