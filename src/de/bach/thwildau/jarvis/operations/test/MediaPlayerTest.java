package de.bach.thwildau.jarvis.operations.test;

import org.junit.Test;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javazoom.jlme.util.Player;

public class MediaPlayerTest {

	@Test
	public void testMediaPlayer(){
		Media media = new Media("Vanessa_Mai-Du_und_Ich.mp3");
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();
	}
}
