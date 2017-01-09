package com.chithra.wikipedia.tokenizer.rules;

import com.chithra.wikipedia.assets.TOKEN_TYPE;
import com.chithra.wikipedia.assets.TokenStream;

public interface TokenizerRule
{
	public void run(TOKEN_TYPE type, TokenStream stream);	
}	
