package de.bach.thwildau.jarvis.operations;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time implements Function {

	private String answer;
	private static Time instance;
	
	
	private Time(String answer){
		this.answer = answer;
	}
	
	public static Time getInstance(String answer){
		if(instance == null){
			instance = new Time(answer);
			return instance;
		} else {
			return instance;
		}
	}
	
	
	@Override
	public String operate() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		String formattedDate = formatter.format(date);
		String answer = this.answer+" "+formattedDate;
		return answer;
		
	}

}
