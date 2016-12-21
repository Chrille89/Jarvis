package de.bach.thwildau.jarvis.operations;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import de.bach.thwildau.jarvis.client.StartClient;
import de.bach.thwildau.jarvis.logging.FileLogger;
import de.bach.thwildau.jarvis.model.GoogleResponse;
import de.bach.thwildau.jarvis.model.LogLevel;

public class WikipediaTracer implements Function {

	private static final String searchUrl = "https://de.wikipedia.org/w/api.php";

	private static WikipediaTracer instance;
	private StartClient client = null;
	private FileLogger logger;
	
	private WikipediaTracer(StartClient client) {
		logger = FileLogger.getLogger(this.getClass().getSimpleName());
		this.client = client;
	}

	public static WikipediaTracer getInstance(StartClient client) {
		if (instance == null) {
			instance = new WikipediaTracer(client);
			return instance;
		} else {
			return instance;
		}
	}

	@Override
	public String operate() {
		String googleToken = client.getGoogleToken();
		client.writeAnswer("Welchen Begriff soll ich für dich nachschlagen?");
		String result = startWikipedia(googleToken);

		return result;

	}

	private String startWikipedia(String googleToken) {
		String strAudio = client.recordingCommando();
		String question = "";

		Response response = client.startGoogleRequest(googleToken, strAudio);

		String result = "";
		
		if (response.getStatus() == 200) {
			logger.log(LogLevel.DEBUG, "Google send Status 200 ;-)");
			GoogleResponse googleResponse = response.readEntity(GoogleResponse.class);

			ObjectMapper mapper = new ObjectMapper();

			if (googleResponse.getResults().size() > 0) {
				question = googleResponse.getResults().get(0).getAlternatives().get(0).getTranscript();
				logger.log(LogLevel.DEBUG, "Question: "+question);
				result = startWikipediaRequest(question);
				return result;
			}
		} else {
			// renew Token!
			logger.log(LogLevel.DEBUG, "Google send: " + response.getStatus() + " :-(");
			logger.log(LogLevel.INFO, "Es ist ein Fehler in der Abarbeitung der Wikipedia-Abfrage aufgetreten! Die Gegenstelle meldet den Fehler-Code: " + response.getStatus());
			client.writeAnswer("Es ist ein Fehler in der Abarbeitung der Wikipedia-Abfrage aufgetreten! Die Gegenstelle meldet den Fehler-Code: " + response.getStatus());
		}
		return result;
	}

	public String startWikipediaRequest(String question) {
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(searchUrl)
				.path("?uselang=de&action=opensearch&search=" + question + "&format=json");

		try {
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
					.accept(MediaType.APPLICATION_JSON_TYPE).header("Content-Type", "application/json");

			String res = invocationBuilder.get(String.class);

			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(res);
			JsonArray array = je.getAsJsonArray();

			String answer = array.get(2).toString().replace("[", "").replace("]", "");
			System.out.println(answer);
			return answer;
		} catch (Exception e) {
			logger.log(LogLevel.WARN, "Error in Wikipedia-Request! "+e.getStackTrace().toString());
		}
		return null;

	}
}