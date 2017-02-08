package de.bach.thwildau.jarvis.operations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

import de.bach.thwildau.jarvis.logging.FileLogger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javazoom.jlme.util.Player;

public class MaiTube implements Function {

	private static String musicDirectory = "maitube/";
	private static MaiTube instance;
	private FileLogger logger;
	private MediaPlayer mediaPlayer;
	private Media media;
	private Stream<Path> paths;

	private MaiTube(String answer) {
		logger = FileLogger.getLogger(this.getClass().getSimpleName());

		try {
			paths = Files.walk(Paths.get(musicDirectory));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static MaiTube getInstance(String answer) {
		if (instance == null) {
			instance = new MaiTube(answer);
			return instance;
		} else {
			return instance;
		}
	}

	@Override
	public String operate() {
		paths.forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				System.out.println(filePath);
				
				media = new Media(filePath.getFileName().toString());
				mediaPlayer = new MediaPlayer(media);
				mediaPlayer.play();

			}
		});
		return null;
	}

}
