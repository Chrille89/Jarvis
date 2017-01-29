package de.bach.thwildau.jarvis.operations.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import javazoom.jlme.util.Player;


public class SchlagerStreamTest {

	@Test
	public void testRadioB2Stream(){
		
		URL url = null;
		try {
			url = new URL("http://mp3.ffh.de/ffhchannels/hqschlager.mp3");
			
			Player player = new Player(url.openStream());
			
			Thread musicThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						player.play();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			musicThread.start();
			
			Thread.sleep(1800000);
			
			player.stop();
			
			musicThread.destroy();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
