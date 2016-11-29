package de.bach.thwildau.jarvis.operations;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import org.xml.sax.InputSource;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class GameStarNews implements Function {

	private static final String rssFeedUrl = "http://www.gamestar.de/news/rss/news.rss";
	private static final String charset = "UTF-8";
	private static GameStarNews instance;
	private String answer=null;
	
	private GameStarNews(String answer){
		this.answer = answer;
	}
	
	
	public static GameStarNews getInstance(String answer){
		if(instance == null){
			instance = new GameStarNews(answer);
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
		        		scanner.nextLine();
		        		scanner.nextLine();
		        		
		        		String description = scanner.nextLine();
		        		news +=" ";
			        	news +=description;
		        	}
		        
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
