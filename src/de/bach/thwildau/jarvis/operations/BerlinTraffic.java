package de.bach.thwildau.jarvis.operations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

public class BerlinTraffic implements Function {

	private static final String rssFeedUrl = "https://viz.berlin.de/rss/iv";
	private static final String charset = "UTF-8";
	private static BerlinTraffic instance;
	private String answer = null;

	private BerlinTraffic(String answer) {
		this.answer = answer;
		// Create a new trust manager that trust all certificates
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Activate the new trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static BerlinTraffic getInstance(String answer) {
		if (instance == null) {
			instance = new BerlinTraffic(answer);
			return instance;
		} else {
			return instance;
		}
	}

	@Override
	public String operate() {
		return this.answer + " " + loadRSSFeeds(rssFeedUrl);
	}

	/*
	 * Load the RSS-Feeds.
	 */
	private String loadRSSFeeds(String feedUrlString) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			InputStream is = new URL(feedUrlString).openConnection().getInputStream();
			InputSource source = new InputSource(is);

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(source);
			List<SyndEntryImpl> entries = feed.getEntries();

			String news = "";

			for (SyndEntryImpl feedEntry : entries) {
				String description = feedEntry.getDescription().getValue();
				String title =  feedEntry.getTitle();

				StringBuilder xmlStringBuilder = new StringBuilder();
				xmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				xmlStringBuilder.append("<class>");
				xmlStringBuilder.append(description);
				xmlStringBuilder.append("</class>");

				ByteArrayInputStream xmlInput = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
				Document doc = builder.parse(xmlInput);

				NodeList list = doc.getElementsByTagName("table");

				String content = "";

				for (int i = 0; i < list.getLength(); i++) {
					content += list.item(i).getTextContent();
				}
				
				content += " ";
				content += title;
				
				NodeList xmlContent = doc.getElementsByTagName("p");

				for (int i = 0; i < xmlContent.getLength(); i++) {
					content += xmlContent.item(i).getTextContent().replace("Abschnitt:", "").replace("(Berlin)", "");
					break;
				}
				news += "... ";
				news += content;
				news += "\n";
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
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
