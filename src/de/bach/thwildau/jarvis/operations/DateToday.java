package de.bach.thwildau.jarvis.operations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DateToday implements Function {

	private String answer;
	private static DateToday instance;
	
	
	private DateToday(String answer){
		this.answer = answer;
	}
	
	
	public static DateToday getInstance(String answer){
		if(instance == null){
			instance = new DateToday(answer);
			return instance;
		} else {
			return instance;
		}
	}
	
	
	@Override
	public String operate() {
		LocalDate date = LocalDate.now(); 
	    DateTimeFormatter df; 
	    System.out.println(date);      // 2016-01-31 
	    df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);      // Sonntag, 31. Januar 2016 
		String formattedDate = date.format(df);
		String answer = this.answer+" "+formattedDate;
		return answer;
	}

}
