package de.bach.thwildau.jarvis.operations;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.xml.sax.InputSource;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

import de.bach.thwildau.jarvis.logging.FileLogger;
import de.bach.thwildau.jarvis.model.LogLevel;

public class TVSpielfilmPrimeTime implements Function {

	private static final String rssFeedUrl = "http://www.tvspielfilm.de/tv-programm/rss/heute2015.xml";
	private static TVSpielfilmPrimeTime instance;
	private String answer = null;
	private FileLogger logger;
	
	private TVSpielfilmPrimeTime(String answer) {
		logger = FileLogger.getLogger(this.getClass().getSimpleName());
		this.answer = answer;
	}

	public static TVSpielfilmPrimeTime getInstance(String answer) {
		if (instance == null) {
			instance = new TVSpielfilmPrimeTime(answer);
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
	 * Load the RSS-Feeds from the URL 'feedUrlString' and put the Title and the
	 * Link in the 'mapFeedEntryToUrl'.
	 */
	private String loadRSSFeeds(String feedUrlString) {
		try {
			InputStream is = new URL(feedUrlString).openConnection().getInputStream();
			InputSource source = new InputSource(is);

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(source);
			List<SyndEntryImpl> entries = feed.getEntries();

			String program = "";

			for (SyndEntryImpl feedEntry : entries) {
				program += "\n";
				String title = feedEntry.getTitle().replace("|", ":");
				String description = feedEntry.getDescription().getValue();
				program +=title+": "+description;
				program += "... ";
			}			
			return program;
		} catch (MalformedURLException e) {
			logger.log(LogLevel.WARN,"Cannot parse RSS-Document! Wrong URL! "+ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (IllegalArgumentException e) {
			logger.log(LogLevel.WARN,"Cannot parse RSS-Document! Illegal Argument! "+ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (FeedException e) {
			logger.log(LogLevel.WARN,"Cannot parse RSS-Document! "+ExceptionUtils.exceptionStackTraceAsString(e));
		} catch (IOException e) {
			logger.log(LogLevel.WARN,"I/O-Error! "+ExceptionUtils.exceptionStackTraceAsString(e));
		}
		return "";
	}
	
}
