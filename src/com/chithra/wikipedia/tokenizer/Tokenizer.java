package com.chithra.wikipedia.tokenizer;

import java.util.LinkedList;
import java.util.List;

import com.chithra.wikipedia.assets.TOKEN_TYPE;
import com.chithra.wikipedia.assets.TokenStream;
import com.chithra.wikipedia.assets.WikiDoc;
import com.chithra.wikipedia.tokenizer.rules.ApostropheRule;
import com.chithra.wikipedia.tokenizer.rules.CapitalizationRule;
import com.chithra.wikipedia.tokenizer.rules.DelimiterRule;
import com.chithra.wikipedia.tokenizer.rules.HyphenRule;
import com.chithra.wikipedia.tokenizer.rules.SpecialCharacterRule;
import com.chithra.wikipedia.tokenizer.rules.StopWordsRule;
import com.chithra.wikipedia.tokenizer.rules.TokenizerRule;
import com.chithra.wikipedia.tokenizer.rules.WhiteSpaceRule;

public class Tokenizer
{
	public static TokenStream tokenize(WikiDoc doc)
	{
		TokenStream tokenStream = new TokenStream(doc);
		for (TOKEN_TYPE type: TOKEN_TYPE.values())
		{
			for (TokenizerRule rule: getTokenizerRules(type))
			{
				rule.run(type, tokenStream);
			}
		}
		return tokenStream;
	}
	
	public static List<TokenizerRule> getTokenizerRules(TOKEN_TYPE tokenType)
	{
		LinkedList<TokenizerRule> rules = new LinkedList<TokenizerRule>();
		switch (tokenType){
			case SECTION:
				rules.add(new ApostropheRule());
				rules.add(new SpecialCharacterRule());
				rules.add(new HyphenRule());
				rules.add(new CapitalizationRule());
				rules.add(new DelimiterRule());
				rules.add(new WhiteSpaceRule());
				rules.add(new StopWordsRule());
				break;
			case CATEGORY:
				rules.add(new ApostropheRule());
				rules.add(new SpecialCharacterRule());
				rules.add(new HyphenRule());
				rules.add(new CapitalizationRule());
				rules.add(new WhiteSpaceRule());
				rules.add(new StopWordsRule());

				break;
		}
		return rules;
	}
}
