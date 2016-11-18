package de.bach.thwildau.jarvis.operations;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class TagesschauNews implements Function {
	
	private static final String rssFeedUrl = "http://www.tagesschau.de/xml/rss2";
	private static TagesschauNews instance;
	private String answer=null;
	
	private TagesschauNews(String answer){
		this.answer = answer;
	}
	
	
	public static TagesschauNews getInstance(String answer){
		if(instance == null){
			instance = new TagesschauNews(answer);
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
		 URL feedUrl;

			try {
				feedUrl = new URL(feedUrlString);
				SyndFeedInput input = new SyndFeedInput();
		        SyndFeed feed = input.build(new XmlReader(feedUrl));
		        List<SyndEntryImpl> entries = feed.getEntries();
		
		        int index=0;
		        String news = "";
		        
		        for(SyndEntryImpl feedEntry : entries){  	
		        	if(index > 5){
		        		break;
		        	}
		        	String description = feedEntry.getDescription().getValue();
			        news +=description;
			        news +="...";
			        index++;
		        }
	            return news;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FeedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
	}
}
