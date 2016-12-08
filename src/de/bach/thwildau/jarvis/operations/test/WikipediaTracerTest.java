package de.bach.thwildau.jarvis.operations;

import org.junit.Test;

import de.bach.thwildau.jarvis.client.StartClient;

public class WikipediaTracerTest {

	
	@Test
	public void testWikipediaTracer(){
		WikipediaTracer tracer = WikipediaTracer.getInstance(new StartClient());
		
		tracer.startWikipediaRequest("Adolf Hitler");
		
	}
	
}
