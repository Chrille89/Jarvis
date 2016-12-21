package de.bach.thwildau.jarvis.operations.test;

import org.junit.Test;

import de.bach.thwildau.jarvis.operations.BerlinTraffic;

class BerlinTrafficTest {

	@Test
	public void testBerlinTrafficRssFeed(){
		System.out.println(BerlinTraffic.getInstance("Verkehr auf Berlins Straßen:").operate());
	}
}
