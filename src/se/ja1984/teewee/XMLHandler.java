package se.ja1984.teewee;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {

	public static Series series;
	public boolean current = false;
	public String currentValue = null;
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		current = true;
		
		if(localName.equals("Series"))
		{
			series = new Series();
		}
	}
	
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException{
		current = false;
		
		if(localName.equals("id")){
			series.setSeriesId(currentValue);
		}
		else if(localName.equals("Actors")){
			series.setActors(currentValue);
		}
		else if(localName.equals("FirstAired")){
			series.setFirstAired(currentValue);
		}
		else if(localName.equals("Genre")){
			series.setFirstAired(currentValue);
		}
		else if(localName.equals("IMDB_ID")){
			series.setImdbId(currentValue);
		}
		else if(localName.equals("Overview")){
			series.setSummary(currentValue);
		}
		else if(localName.equals("Rating")){
			series.setRating(currentValue);
		}
		else if(localName.equals("SeriesName")){
			series.setName(currentValue);
		}
		else if(localName.equals("Status")){
			series.setStatus(currentValue);
		}
		else if(localName.equals("banner")){
			series.setImage(currentValue);
		}
		else if(localName.equals("fanart")){
			series.setHeader(currentValue);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException{
		if(current){
			currentValue = new String(ch,start,length);
			current = false;
		}
	}

}


