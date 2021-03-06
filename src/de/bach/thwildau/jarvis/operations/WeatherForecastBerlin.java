package de.bach.thwildau.jarvis.operations;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.xml.sax.InputSource;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

import de.bach.thwildau.jarvis.logging.FileLogger;
import de.bach.thwildau.jarvis.model.LogLevel;
import sun.java2d.x11.X11SurfaceDataProxy.Opaque;

public class WeatherForecastBerlin implements Function {

	private static final String rssFeedUrl = "http://www.wetter24.de/rss/";
	private static final String charset = "UTF-8";
	private static WeatherForecastBerlin instance;
	private String answer = null;
	private FileLogger logger;

	private WeatherForecastBerlin(String answer) {
		logger = FileLogger.getLogger(this.getClass().getSimpleName());
		this.answer = answer;
	}

	public static WeatherForecastBerlin getInstance(String answer) {
		if (instance == null) {
			instance = new WeatherForecastBerlin(answer);
			return instance;
		} else {
			return instance;
		}
	}

	@Override
	public String operate() {
		return answer + " " + loadRSSFeeds(rssFeedUrl);
	}

	/*
	 * Load the RSS-Feeds.
	 */
	private String loadRSSFeeds(String feedUrlString) {

		InputStream is = null;
		try {
			is = new URL(feedUrlString).openConnection().getInputStream();
		} catch (MalformedURLException e) {
			logger.log(LogLevel.WARN,"Cannot parse RSS-Document! The URL is wrong! "+ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (IOException e) {
			logger.log(LogLevel.WARN,"I/O-Error! "+ExceptionUtils.exceptionStackTraceAsString(e));
		}
		InputSource source = new InputSource(is);

		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = null;
		try {
			feed = input.build(source);
		} catch (IllegalArgumentException e) {
			logger.log(LogLevel.WARN,"Cannot parse RSS-Document! Illegal Argument! "+ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (FeedException e) {
			logger.log(LogLevel.WARN,"Cannot parse RSS-Document! "+ExceptionUtils.exceptionStackTraceAsString(e));
		}
		List<SyndEntryImpl> entries = feed.getEntries();

		String news = "";

		List<String> weatherList = null;
		
		for (SyndEntryImpl feedEntry : entries) {
			String description = feedEntry.getDescription().getValue();

			String[] array = description.split(">");

			weatherList = new ArrayList<String>();

			String day = array[4].replace("</big", "");
			String tempFrom = array[22].replace("</font", "");
			String tempTo = array[24].replace("</font", "");
			String weatherFrom = array[47].substring(array[47].indexOf("alt=")).replace("alt=", "").replace("'", "");
			String weatherTo = array[50].substring(array[50].indexOf("alt=")).replace("alt=", "").replace("'", "");
			weatherList.add(day + " " + tempFrom + " bis" + " " + tempTo + " " + weatherFrom+ " bis "+weatherTo);

			day = array[8].replace("</big", "");
			tempFrom = array[28].replace("</font", "");
			tempTo = array[30].replace("</font", "");
			weatherFrom = array[53].substring(array[53].indexOf("alt=")).replace("alt=", "").replace("'", "");
			weatherTo = array[56].substring(array[56].indexOf("alt=")).replace("alt=", "").replace("'", "");
			weatherList.add(day + " " + tempFrom + " bis" + " " + tempTo + " "+weatherFrom+ " bis "+weatherTo);

			day = array[12].replace("</big", "");
			tempFrom = array[34].replace("</font", "");
			tempTo = array[36].replace("</font", "");
			weatherFrom = array[59].substring(array[59].indexOf("alt=")).replace("alt=", "").replace("'", "");
			weatherTo = array[62].substring(array[62].indexOf("alt=")).replace("alt=", "").replace("'", "");
			weatherList.add(day + " " + tempFrom + " bis" + " " + tempTo + " " +weatherFrom+ " bis "+weatherTo);

			day = array[16].replace("</big", "");
			tempFrom = array[40].replace("</font", "");
			tempTo = array[42].replace("</font", "");
			weatherFrom = array[65].substring(array[65].indexOf("alt=")).replace("alt=", "").replace("'", "");
			weatherTo = array[68].substring(array[68].indexOf("alt=")).replace("alt=", "").replace("'", "");
			weatherList.add(day + " " + tempFrom + " bis" + " " + tempTo + " " +weatherFrom+ " bis "+weatherTo);

			//System.out.println(weatherList);
			break;
		}
		
		for(String weatherDatas : weatherList){
			news += "... ";
			news += weatherDatas;
			news += "\n";
		}

		return news;
	}
	
	public static void main(String[] args) {
		System.out.println(new WeatherForecastBerlin("Das Wetter in Berlin: ").operate());
		
		
		
	}

}
