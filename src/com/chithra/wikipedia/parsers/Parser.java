package com.chithra.wikipedia.parsers;

import java.util.Collection;
import org.xml.sax.helpers.DefaultHandler;
import com.chithra.wikipedia.assets.WikiDoc;
import org.xml.sax.Attributes;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

public class Parser extends DefaultHandler
{
	private boolean btitle;
	private boolean bid;
	private boolean bcontent;
	private WikiDoc wiki;
	private String content;
	private Collection<WikiDoc> wikiDocList;
	
	public void parseWikiXml(String filePath,Collection<WikiDoc> wikiCollection)
	{
		wikiDocList=wikiCollection;
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(filePath, this);
		} catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	public void startElement(String uri, String localName,String qName,
			Attributes attributes) throws SAXException
	{		
		if(qName.equalsIgnoreCase("page"))
		{
			wiki=new WikiDoc();
			content="";
		}
		if(qName.equalsIgnoreCase("title"))
			btitle=true;
		if(qName.equalsIgnoreCase("id"))
			bid=true;
		if(qName.equalsIgnoreCase("text"))
			bcontent=true;		
	}
	
	public void endElement(String uri, String localName,
			String qName) throws SAXException
	{
		if(qName.equalsIgnoreCase("title"))
			btitle=false;
		if(qName.equalsIgnoreCase("id"))
			bid=false;
		if(qName.equalsIgnoreCase("text"))
		{
			bcontent=false;		
			content = WikiParser.parseTagFormatting(content);
			content = WikiParser.parseTemplates(content);
			content = WikiParser.parseQuotes(content);
			content = WikiParser.parseLists(content);
			content = WikiParser.parseComments(content);
			content = WikiParser.parseCategories(wiki, content);
			content = WikiParser.parseLinks(wiki, content);
			content = WikiParser.parseSections(wiki, content);			
		}
		if(qName.equalsIgnoreCase("page"))
		{
			wikiDocList.add(wiki);
		}
	}
	
	public void characters(char ch[], int start, int length) throws SAXException
	{
		if(btitle)
		{
			wiki.title = new String(ch, start, length);
		}
		if(bid)
		{
			wiki.id = Integer.parseInt(new String(ch, start, length));
		}
		if(bcontent)
		{	
			content += new String(ch, start, length);
		}
	}
}	