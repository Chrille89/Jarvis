package de.bach.thwildau.jarvis.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WeatherBerlinHandler extends DefaultHandler {

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		super.endElement(uri, localName, qName);
		System.out.println("ENDELEMENT"+"uri: "+uri+";"+"localName: "+localName+";"+"qName: "+qName);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		System.out.println("ENDELEMENT"+"uri: "+uri+";"+"localName: "+localName+";"+"qName: "+qName+";"+"Attributes: "+attributes);
		
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		System.out.println("char: "+ch+"Start: "+start+"Length: "+length);
		
	}

	
}
