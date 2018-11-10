package de.bach.thwildau.jarvis.operations.test;

import org.junit.Test;

import de.bach.thwildau.jarvis.operations.RbbNews;
import de.bach.thwildau.jarvis.operations.TagesschauNews;

public class TagesschauTest {

	@Test
	public void testTagesschauNews(){
		System.out.println(TagesschauNews.getInstance("Die Nachrichten: ").operate());
	}
}
