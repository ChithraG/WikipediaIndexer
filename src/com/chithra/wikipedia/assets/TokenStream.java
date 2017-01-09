package com.chithra.wikipedia.assets;

import java.util.HashMap;
import java.util.LinkedList;

public class TokenStream
{
	public WikiDoc doc;
	public HashMap<TOKEN_TYPE, LinkedList<String>> tokensByType;
	
	public TokenStream(WikiDoc doc)
	{
		this.doc = doc;
		tokensByType = new HashMap<>();
		LinkedList<String> categoryList = new LinkedList<String>();
		LinkedList<String> sectionList = new LinkedList<String>();
		categoryList.addAll(doc.getCategories());
		sectionList.addAll(doc.getSectionsText());
		tokensByType.put(TOKEN_TYPE.CATEGORY, categoryList);
		tokensByType.put(TOKEN_TYPE.SECTION, sectionList);
	}
	
	public HashMap<String, Integer> getTokenOccurenceMap(TOKEN_TYPE tokenType)
	{
		LinkedList<String> tokens = tokensByType.get(tokenType);
		HashMap<String, Integer> termFrequency = new HashMap<String, Integer>();
		int a = 1;
		for(String token: tokens)
		{
			if(termFrequency.containsKey(token))
				a = termFrequency.get(token) + 1;
			termFrequency.put(token, a);
		}
		return termFrequency;
	}
}
