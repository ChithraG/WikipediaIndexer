package com.chithra.wikipedia.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.chithra.wikipedia.assets.WikiDoc;

public class WikiParser
{
	public static String parseCategories(WikiDoc doc, String content)
	{
		Matcher matcher = runPatternMatcher("\\[\\[Category\\:(.*?)(\\| ){0,1}\\]\\]", content);
		while(matcher.find())
		{
			String category = matcher.group(1);
			doc.addCategory(category);
		}
		return matcher.replaceAll("");
	}
	
	public static String parseComments(String content)
	{
		return content.replaceAll("<\\!\\-\\-(.*?)\\-\\->", "");
	}
	
	public static String parseLists(String content)
	{
		return content.replaceAll("\\*|\\#", "");
	}
	
	public static String parseQuotes(String content){
		return content.replaceAll("'{2,}", "");
	}
	
	public static String parseTemplates(String content)
	{
		Matcher matcher = runPatternMatcher("\\{\\{(.*?)\\}\\}", content, Pattern.DOTALL);
		return matcher.replaceAll("");
	}
	
	public static String parseTagFormatting(String content)
	{
		Matcher matcher = runPatternMatcher("<.*?>(.*?)<\\/.*?>|<.*?\\/>", content);
		return matcher.replaceAll("$1");
	}
	
	public static String parseSections(WikiDoc doc, String content)
	{
		Matcher matcher = runPatternMatcher("((\\=){2,})([\\s\\S]+?)(\\1)(((?!\\==)[\\s\\S])*)", content);
		while(matcher.find())
		{
			String title=matcher.group(3);
			String text=matcher.group(5);
			doc.addSection(title,text);
		}
		return matcher.replaceAll("");
	}
	
	public static String parseLinks(WikiDoc doc, String content)
	{
		content = parseLinksWithPattern("\\[\\[(word)\\]\\]", doc, content, 1); // [[band leader]]
		content = parseLinksWithPattern("\\[\\[(word), word\\|word\\]\\]", doc, content, 1); //[[Seattle, Washington|]]
		content = parseLinksWithPattern("\\[\\[[a-zA-Z0-9_ .'\\(\\)]*\\|(word)\\]\\]", doc, content, 1); // [[Texas|Lone Star State]]
		content = parseLinksWithPattern("\\[\\[Wikipedia:(word)\\|\\]\\]", doc, content, 1); // [[Wikipedia:Manual of Style|]]
		content = parseLinksWithPattern("\\[\\[Wikipedia:(word)\\(word\\)\\|\\]\\]", doc, content, 1); // [[Wikipedia:Manual of Style (headings)|]]
		content = parseLinksWithPattern("\\[\\[Wiktionary:(word)\\|\\]\\]", doc, content, 1); 
		content = parseLinksWithPattern("\\[\\[Wiktionary:(word)\\(word\\)\\|\\]\\]", doc, content, 1);
		content = parseLinksWithPattern("\\[[^\\s]*? (.*?)\\]", doc, content, 1); // external links: [http://www.russconway.co.uk/ Russ Conway]		
		return content;
	}
	
	private static String parseLinksWithPattern(String pattern, WikiDoc doc, String content, int groupNum)
	{
		String word = "[a-zA-Z0-9_ '-]*";
		pattern = pattern.replaceAll("word", word);
		Matcher matcher = runPatternMatcher(pattern, content);
		while(matcher.find())
		{
			doc.links.add(matcher.group(groupNum));
		}			 
		return matcher.replaceAll("$" + groupNum);
	}
	
	private static Matcher runPatternMatcher(String patternString, String content)
	{
		return runPatternMatcher(patternString, content, Pattern.MULTILINE);
	}
	
	private static Matcher runPatternMatcher(String patternString, String content, int config)
	{
		Pattern pattern = Pattern.compile(patternString, config);
		Matcher matcher = pattern.matcher(content);		
		return matcher;
	}
}