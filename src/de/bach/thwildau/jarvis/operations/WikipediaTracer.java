package de.bach.thwildau.jarvis.operations;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bach.thwildau.jarvis.client.StartClient;
import de.bach.thwildau.jarvis.model.GoogleResponse;
import sun.awt.CharsetString;

public class WikipediaTracer implements Function {

	private static final String searchUrl = "https://de.wikipedia.org/w/api.php";

	private static WikipediaTracer instance;
	private String answer = null;
	private StartClient client = null;

	private WikipediaTracer(StartClient client) {
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

		List<String> results = startWikipedia(googleToken);

		return results.get(1);

	}

	private List<String> startWikipedia(String googleToken) {
		String strAudio = client.recordingCommando();
		String question = "";

		Response response = client.startGoogleRequest(googleToken, strAudio);

		if (response.getStatus() == 200) {
			System.out.println("Google send Status 200 ;-)");
			GoogleResponse googleResponse = response.readEntity(GoogleResponse.class);

			ObjectMapper mapper = new ObjectMapper();
			try {
				String strResponse = mapper.writeValueAsString(googleResponse);
				System.out.println("Response: " + strResponse);
			} catch (JsonProcessingException e1) {
				System.err.println("Error parsing GoogleResponse-Class to JSON-Format!");
				e1.printStackTrace();
			}

			if (googleResponse.getResults().size() == 1) {
				question = googleResponse.getResults().get(0).getAlternatives().get(0).getTranscript();
				System.out.println("Question: " + question);
				if (question.equalsIgnoreCase("Wikipedia beenden")) {
					System.out.println("Beende Wikipedia!");
					return null;
				}
				List<String> results = startWikipediaRequest(question);
				return results;

			}

		}
		return null;
	}

	public List<String> startWikipediaRequest(String question) {

		List<List<String>> results = null;

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(searchUrl).path("?action=opensearch&search=Donald_Trump&format=json&callback=?");

		try {
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("Content-Type", "application/json;charset=UTF-8");

			Response response = invocationBuilder.get();
			
			Scanner scanner = new Scanner((InputStream) response.getEntity(),"utf-8");
			System.out.println(Charset.availableCharsets());
			while(scanner.hasNext()){
				String s = new String(scanner.next().getBytes(),Charset.forName("UTF-8"));
				System.out.println(s);
			}
			
	
			for(List<String> l : results){
				for(String s : l){
					System.out.println(s);
				}
			}
			
			return null;
		} catch (Exception e) {
			// writeLog("ERROR: ", "Error in Request "+index+". Try again..."+e.getMessage());
			System.err.println("Error in Wikipedia-Request!");
			e.printStackTrace();
			return null;
		}

	}
}