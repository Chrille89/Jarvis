package de.bach.thwildau.jarvis.operations;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.xml.sax.InputSource;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

import de.bach.thwildau.jarvis.logging.FileLogger;
import de.bach.thwildau.jarvis.model.LogLevel;

public class GermanyTraffic implements Function {

	private static final String rssFeedUrl = "http://www.deutschlandradio.de/verkehrsmeldungen.438.de.rss";
	private static final String charset = "UTF-8";
	private static GermanyTraffic instance;
	private String answer = null;
	private FileLogger logger;
	
	private GermanyTraffic(String answer) {
		logger = FileLogger.getLogger(this.getClass().getSimpleName());
		this.answer = answer;
	}

	public static GermanyTraffic getInstance(String answer) {
		if (instance == null) {
			instance = new GermanyTraffic(answer);
			return instance;
		} else {
			return instance;
		}
	}
	
	@Override
	public String operate() {
		String feed = loadRSSFeeds(rssFeedUrl);
		return this.answer+" "+feed;
	}
	
	/*
	 * Load the RSS-Feeds.
	 */
	private String loadRSSFeeds(String feedUrlString) {
		try {
			InputStream is = new URL(feedUrlString).openConnection().getInputStream();
			InputSource source = new InputSource(is);

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(source);
			List<SyndEntryImpl> entries = feed.getEntries();

			String news = "";

			for (SyndEntryImpl feedEntry : entries) {
				String description = feedEntry.getDescription().getValue();
				news +="... ";
				news += description;
				news +="\n";
			}
			
			return news;
		} catch (MalformedURLException e) {
			logger.log(LogLevel.WARN,"Cannot parse RSS-Document! Wrong URL! "+e.getStackTrace().toString());
		} catch (IllegalArgumentException e) {
			logger.log(LogLevel.WARN,"Cannot parse RSS-Document! Illegal Argument! "+e.getStackTrace().toString());
		} catch (FeedException e) {
			logger.log(LogLevel.WARN,"Cannot parse RSS-Document! "+e.getStackTrace().toString());
		} catch (IOException e) {
			logger.log(LogLevel.WARN,"I/O-Error! "+e.getStackTrace().toString());
		}
		return "";
	}

}
