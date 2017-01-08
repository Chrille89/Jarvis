package de.bach.thwildau.jarvis.operations.test;

import org.junit.Test;

import de.bach.thwildau.jarvis.operations.TVSpielfilmPrimeTime;

public class TVSpielfilmTest {

	@Test
	public void testTVSpielfilm(){
		System.out.println(TVSpielfilmPrimeTime.getInstance("Heute im Fernsehen:").operate());
	}
}
