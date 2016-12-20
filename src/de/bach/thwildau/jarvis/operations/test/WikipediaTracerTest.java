package de.bach.thwildau.jarvis.operations.test;

import org.junit.Test;

import de.bach.thwildau.jarvis.client.StartClient;
import de.bach.thwildau.jarvis.operations.WikipediaTracer;

public class WikipediaTracerTest {

	
	@Test
	public void testWikipediaTracer(){
		WikipediaTracer tracer = WikipediaTracer.getInstance(new StartClient());
		
		tracer.startWikipediaRequest("Angela Merkel");
		
	}
	
}
