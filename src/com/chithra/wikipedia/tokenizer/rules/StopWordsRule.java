package com.chithra.wikipedia.tokenizer.rules;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

import com.chithra.wikipedia.assets.TOKEN_TYPE;
import com.chithra.wikipedia.assets.TokenStream;

public class StopWordsRule implements TokenizerRule
{
	public void run(TOKEN_TYPE type, TokenStream stream)
	{
		LinkedList<String> tokens = stream.tokensByType.get(type);
		ListIterator<String> tokenIterator = tokens.listIterator();
		
		while(tokenIterator.hasNext())
		{	
			String[] stopWords = {"of", "the", "his", "also", "this", "is", "do", "not", "a", "an", "in", "to", "who", "and", "at", "for", "on", "with", "was", "from", "he", "by", "him"};
			String token = tokenIterator.next();
			if(Arrays.asList(stopWords).indexOf(token) != -1)
				tokenIterator.remove();
		}
	}
}
