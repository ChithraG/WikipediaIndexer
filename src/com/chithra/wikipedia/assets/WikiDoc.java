package com.chithra.wikipedia.assets;
import java.io.Serializable;
import java.util.*;

public class WikiDoc implements Serializable
{
	public String title;
	public int id;
	public String author;
	public List<Section> sections;
	public List<String> categories;
	public List<String> links;
	
	public void addSection(String title, String text)
	{
		Section section = new Section(title, text);
		sections.add(section);
	}
	public void addCategory(String text)
	{	
		categories.add(text);
	}
	public WikiDoc()
	{
		sections = new ArrayList<Section>();
		categories = new ArrayList<String>();
		links = new ArrayList<String>();
	}
	
	public static class Section  implements Serializable
	{
		public String title;
		private String text;
		public Section(String title, String text)
		{
			this.title=title;
			this.text=text;
		}
		
		public String getText()
		{
			return text;
		}
		
		public String getTitle()
		{
			return title;
		}
	}
	
	public List<String> getCategories() {
		return categories;
	}
	
	public List<String> getSectionsText() {
		ArrayList<String> sectionsText = new ArrayList<String>();
		for (Section section: sections){
			sectionsText.add(section.text);
		}
		return sectionsText;
	}
}
