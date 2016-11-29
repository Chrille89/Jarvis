package de.bach.thwildau.jarvis.operations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bach.thwildau.jarvis.client.StartClient;
import de.bach.thwildau.jarvis.model.GoogleResponse;

public class Youtuber implements Function {

	private static Youtuber instance;
	private StartClient client = null;

	private Youtuber(StartClient client) {
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
		String googleToken = client.getGoogleToken();
		startYoutube(googleToken);
		return "Youtuber wurde beendet!";
	}

	private void startYoutube(String googleToken) {
		client.writeAnswer("Was möchtest du hoeren?");
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

			if (googleResponse.getResults().size() > 0) {
				question = googleResponse.getResults().get(0).getAlternatives().get(0).getTranscript();
				System.out.println("Question: " + question);
				if (question.equalsIgnoreCase("YouTube beenden")) {
					System.out.println("Beende Youtube!");
					return;
				}
				String cmd = null;
				try {
					cmd = "sudo ./downloadAndPlayYoutubeVideo.sh " + question;
					System.out.println("Execute Command " + cmd);
					Runtime.getRuntime().exec(cmd).waitFor();
				} catch (IOException e) {
					System.err.println("Error execute commando '" + cmd + "'");
					e.printStackTrace();
				} catch (InterruptedException e) {
					System.err.println("Error execute commando '" + cmd + "'");
					e.printStackTrace();
				}
			}
			// es wurde nichts gesagt -> weiter bis 'YouTube beenden'
			startYoutube(googleToken);
		} else {
			// kein 200-Status -> nochmal versuchen!
			String newToken = client.getGoogleToken();
			startYoutube(newToken);
		}

	}

}
