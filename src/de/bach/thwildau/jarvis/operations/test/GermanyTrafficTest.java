package de.bach.thwildau.jarvis.operations.test;

import org.junit.Test;

import de.bach.thwildau.jarvis.operations.GermanyTraffic;

public class GermanyTrafficTest {

	@Test
	public void testGetTrafficRSSFeed(){
		System.out.println(GermanyTraffic.getInstance("Verkehrsmeldungen in Deutschland:").operate());
	}
}
