package de.bach.thwildau.jarvis.operations.test;

import org.junit.Test;

import de.bach.thwildau.jarvis.operations.BerlinTraffic;

public class BerlinTrafficTest {

	@Test
	public void testBerlinTrafficRssFeed(){
		System.out.println(BerlinTraffic.getInstance("Verkehr auf Berlins Straßen:").operate());
	}
}
