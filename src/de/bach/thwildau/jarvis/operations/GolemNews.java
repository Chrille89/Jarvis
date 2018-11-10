package de.bach.thwildau.jarvis.operations;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.xml.sax.InputSource;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

import de.bach.thwildau.jarvis.logging.FileLogger;
import de.bach.thwildau.jarvis.model.LogLevel;

public class GolemNews implements Function {

	private static final String rssFeedUrl = "https://rss.golem.de/rss.php?feed=RSS2.0";
	private static final String charset = "UTF-8";
	private static GolemNews instance;
	private String answer=null;
	private FileLogger logger;
	
	private GolemNews(String answer){
		logger = FileLogger.getLogger(this.getClass().getSimpleName());
		this.answer = answer;
	}
	
	
	public static GolemNews getInstance(String answer){
		if(instance == null){
			instance = new GolemNews(answer);
			return instance;
		} else {
			return instance;
		}
	}
	
	@Override
	public String operate() {
		return answer+" "+loadRSSFeeds(rssFeedUrl);
	}
	
	/*
	 * Load the RSS-Feeds from the URL 'feedUrlString' and
	 * put the Title and the Link in the 'mapFeedEntryToUrl'.
	 */
	private String loadRSSFeeds(String feedUrlString) {
			try {
		        InputStream is = new URL(feedUrlString).openConnection().getInputStream();
		        InputSource source = new InputSource(is);
		        
				SyndFeedInput input = new SyndFeedInput();
		        SyndFeed feed = input.build(source);
		        List<SyndEntryImpl> entries = feed.getEntries();
		
		        String news = "";
		     
		        Scanner scanner;
		        
		        for(SyndEntryImpl feedEntry : entries){
		        	scanner = new Scanner(feedEntry.getDescription().getValue());
	
		        	if(scanner.hasNextLine()){
		        		//scanner.nextLine();
		        		//scanner.nextLine();
		        		
		        		String description = scanner.nextLine();
		        		description = description.substring(0, description.indexOf("("));
		        		news += " ...";
		        		news +="\n";
			        	news +=description;
		        	}
		        
		        }
	            return news;
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
	
	public static void main(String[] args) {
		System.out.println(new GolemNews("Die Golem-Nachrichten: ").operate());
	}
}
