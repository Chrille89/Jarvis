package de.bach.thwildau.jarvis.operations.test;

import org.junit.Test;

import de.bach.thwildau.jarvis.operations.Function;
import de.bach.thwildau.jarvis.operations.JobTicker;

public class JobTickerTest {

	@Test
	public void testJobTicker(){
		Function function = JobTicker.getInstance("Folgende Jobs liegen mir vor: ");
		String feeds = function.operate();
		System.out.println(feeds);
		
	}
	
	
}
