package com.chithra.wikipedia.tokenizer.rules;

import java.util.LinkedList;
import java.util.ListIterator;

import com.chithra.wikipedia.assets.TOKEN_TYPE;
import com.chithra.wikipedia.assets.TokenStream;

public class WhiteSpaceRule implements TokenizerRule
{
	@Override
	public void run(TOKEN_TYPE type, TokenStream stream)
	{
		LinkedList<String> tokens = stream.tokensByType.get(type);
		ListIterator<String> tokenIterator = tokens.listIterator();
		
		while (tokenIterator.hasNext())
		{
			String token = tokenIterator.next();
			String[] splitTokens = token.split("\\s{1,}");
			tokenIterator.remove();
			for(String split: splitTokens)
			{
				if(!split.equals(""))
					tokenIterator.add(split);
			}
		}
	}

}
