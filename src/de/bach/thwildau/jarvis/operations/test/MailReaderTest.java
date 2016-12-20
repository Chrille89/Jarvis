package de.bach.thwildau.jarvis.operations.test;

import org.junit.Test;

import de.bach.thwildau.jarvis.operations.MailReader;

public class MailReaderTest {

	@Test
	public void testEmailReader(){
		
		System.out.println(MailReader.getInstance("Deine Mails:").operate());
		
	}
}
