package de.bach.thwildau.jarvis.operations.test;

import org.junit.Test;

import de.bach.thwildau.jarvis.operations.RbbNews;

public class RbbNewsTest {

	@Test
	public void testRbbNews(){
		System.out.println(RbbNews.getInstance("Die Nachrichten vom RBB: ").operate());
	}
}
