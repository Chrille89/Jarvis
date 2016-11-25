package de.bach.thwildau.jarvis.operations;

import java.io.IOException;

public class EmulationStation implements Function {
	
	private static EmulationStation instance;
	private String answer=null;
	
	private EmulationStation(String answer){
		this.answer = answer;
	}
	
	
	public static EmulationStation getInstance(String answer){
		if(instance == null){
			instance = new EmulationStation(answer);
			return instance;
		} else {
			return instance;
		}
	}
	@Override
	public String operate() {
		String cmd = "./startEmulationStation.sh";
		
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			String error = "Konnte die Spielekonsole nicht starten!";
			System.err.println(error);
			e.printStackTrace();
			return error;
		}
		
		return this.answer;
	}

}
