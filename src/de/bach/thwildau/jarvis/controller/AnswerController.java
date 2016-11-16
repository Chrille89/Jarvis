package de.bach.thwildau.jarvis.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

import de.bach.thwildau.jarvis.model.GoogleRequest;
import de.bach.thwildau.jarvis.model.GoogleResponse;
import de.bach.thwildau.jarvis.operations.DateToday;
import de.bach.thwildau.jarvis.operations.Function;
import de.bach.thwildau.jarvis.operations.GameStarNews;
import de.bach.thwildau.jarvis.operations.GameStarVideos;
import de.bach.thwildau.jarvis.operations.TagesschauNews;
import de.bach.thwildau.jarvis.operations.Time;

@Path("/question")
public class AnswerController {

	private static Map<String,Function> operations;
	private static Properties prop;
	
	static{
		operations = new HashMap<>();
		prop = new Properties();
		InputStream stream = AnswerController.class.getClassLoader().getResourceAsStream("string.properties");
		try {
			prop.load(stream);
			operations.put(prop.getProperty("question.time1"), Time.getInstance(prop.getProperty("answer.time")));
			operations.put(prop.getProperty("question.time2"), Time.getInstance(prop.getProperty("answer.time")));
			operations.put(prop.getProperty("question.date1"), DateToday.getInstance(prop.getProperty("answer.date")));
			operations.put(prop.getProperty("question.date2"), DateToday.getInstance(prop.getProperty("answer.date")));
			operations.put(prop.getProperty("question.date3"), DateToday.getInstance(prop.getProperty("answer.date")));
			
			operations.put(prop.getProperty("question.gamestarnews1"), GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));
			operations.put(prop.getProperty("question.gamestarnews2"), GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));
			operations.put(prop.getProperty("question.gamestarnews3"), GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));
			operations.put(prop.getProperty("question.gamestarnews4"), GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));
			operations.put(prop.getProperty("question.gamestarnews5"), GameStarNews.getInstance(prop.getProperty("answer.gamestarnews")));
		
			operations.put(prop.getProperty("question.gamestarvideos1"), GameStarVideos.getInstance(prop.getProperty("answer.gamestarvideos")));
			operations.put(prop.getProperty("question.gamestarvideos2"), GameStarVideos.getInstance(prop.getProperty("answer.gamestarvideos")));
			
			// Tagesschau
			for(int i=1; i<22;i++){
				operations.put(prop.getProperty("question.tagesschaunews"+String.valueOf(i)), TagesschauNews.getInstance(prop.getProperty("answer.tagesschau")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@GET
	@Path("/test")
	public String test() {
		return "Jarvis waiting for questions!";
	}

	@POST
	@Path("/post")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAnswer(@HeaderParam("Authorization") String token, GoogleRequest googleRequest){
		
	
		System.out.println("Google-Token: "+token);
		
		Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFilter.class ) );
		WebTarget webTarget = client.target("https://speech.googleapis.com").path("/v1beta1/speech:syncrecognize");
		
	    ObjectMapper mapper = new ObjectMapper();
	    
	    String json = null;
		try {
			json = mapper.writeValueAsString(googleRequest);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    System.out.println(json);
	    
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON)
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer "+token);
			
		Response response = invocationBuilder.post(Entity.entity(json, MediaType.APPLICATION_JSON));
			
		//Set Response.Status.OK;
		  if (response.getStatus() == 200) {
			  GoogleResponse googleResponse = response.readEntity(GoogleResponse.class);
		     
		     if(googleResponse.getResults().size() == 1){
		        String question = googleResponse.getResults().get(0).getAlternatives().get(0).getTranscript();
		     
		        if(operations.get(question) != null){
		        	Function function = operations.get(question);
		        	String answer = function.operate();
		        	return Response.ok(answer).build();
		        } else {
		        	 String cmdNotFound = prop.getProperty("question.notfound");
					 return Response.ok(cmdNotFound).build();
		        }
		     }  
			  return Response.ok("").build();
		  }
		  String error = prop.getProperty("question.google.error");
		  return Response.ok(error).build();
	}    
}

