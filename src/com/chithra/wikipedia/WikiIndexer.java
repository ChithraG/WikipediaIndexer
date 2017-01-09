package com.chithra.wikipedia;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

import com.chithra.wikipedia.assets.TokenStream;
import com.chithra.wikipedia.assets.WikiDoc;
import com.chithra.wikipedia.assets.WikiResult;
import com.chithra.wikipedia.parsers.Parser;
import com.chithra.wikipedia.tokenizer.Tokenizer;

public class WikiIndexer
{	
	public static void main(String args[]) throws IOException
	{
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		IREngine newEngine = new IREngine();
		String fileName="files/five_entries.xml";
		Parser parser=new Parser();
		LinkedList<WikiDoc> wikiDocList = new LinkedList<WikiDoc>();
		parser.parseWikiXml(fileName, wikiDocList);		
		int option = 0;
		IREngine engine = new IREngine();		
		String decision = "yes";
		
		while(decision.equalsIgnoreCase("yes"))
		{
			System.out.println("Please select one of the below\n1. Index \n2. Search \n3. Exit");
			System.out.println("Enter a number");
			option = Integer.parseInt(b.readLine());
			switch(option)
			{
				case 1:
					WikiIndexer.runIndexer(wikiDocList, engine);
					System.out.println("Finished Indexing. Thank you!!");
					decision = "no";
				break;
				case 2:		
					try 
					{
						File file = new File("files/saved1.ser");
						if(!file.exists())
						{
							System.out.println("The IR Engine is not setup, please run indexer");
							break;
						}
						else
						{
						newEngine = engine.readFromFile("files/saved1.ser");
						}
					}catch (ClassNotFoundException e) 
					{
						e.printStackTrace();
					}
					System.out.println("Enter the search term");
					String term = b.readLine();
					runSearch(newEngine, term, wikiDocList);		
					System.out.println("Do you want to continue yes/no?");
					decision = b.readLine();
				break;
				case 3:
					System.out.println("Exited from program");
					decision = "no";
				break;
			}
		}
	}
	
	public static void runIndexer(LinkedList<WikiDoc> wikiDocList, IREngine engine) throws IOException
	{
		for(WikiDoc wd:wikiDocList)
		{
			TokenStream tokenStream = Tokenizer.tokenize(wd);		
			engine.addTokenStream(tokenStream);
			engine.writeToFile(engine, "files/saved1.ser");			
		}
	}
	
	public static void runSearch(IREngine newEngine, String term, LinkedList<WikiDoc> wikiDocList)
	{
		ArrayList<WikiResult> wikiResult = newEngine.runQuery(wikiDocList, term);
		for(WikiResult wiki: wikiResult)
		{	
			if(wiki.score!=0.0)
			{
				System.out.println("Title: "+wiki.wiki.title);					
				System.out.println("Score: "+wiki.score);
			}
			else
				System.out.println("Term not found");
		}
	}
}
