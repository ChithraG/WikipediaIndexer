package com.chithra.wikipedia;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.chithra.wikipedia.assets.TOKEN_TYPE;
import com.chithra.wikipedia.assets.TokenStream;
import com.chithra.wikipedia.assets.WikiDoc;
import com.chithra.wikipedia.assets.WikiResult;

public class IREngine implements Serializable
{
	private HashMap<Integer, WikiDoc> documentDictionary;
	private HashMap<TOKEN_TYPE, TermDictionary> termDictionaryByType;
	private HashMap<TOKEN_TYPE, PostingsList> postingsListByType;
	
	public IREngine()
	{
		documentDictionary = new HashMap<>();
		termDictionaryByType = new HashMap<>();
		termDictionaryByType.put(TOKEN_TYPE.CATEGORY, new TermDictionary());
		termDictionaryByType.put(TOKEN_TYPE.SECTION, new TermDictionary());
		postingsListByType = new HashMap<>();
		postingsListByType.put(TOKEN_TYPE.CATEGORY, new PostingsList());
		postingsListByType.put(TOKEN_TYPE.SECTION, new PostingsList());
	}	
	public void writeToFile(IREngine engine, String fileName) throws IOException
	{
		File file = new File("files/saved1.ser");
		if(!file.exists())
		{
			file.createNewFile();
		}
		FileOutputStream oByteArrayOutputStream = new FileOutputStream(fileName);
	    ObjectOutputStream out = new ObjectOutputStream(oByteArrayOutputStream);
	    out.writeObject(engine);
	    out.close();
	}
	
	public IREngine readFromFile(String fileName) throws IOException,ClassNotFoundException
	{
		IREngine inputEngine;
		FileInputStream oByteArrayInputStream = new FileInputStream(fileName);
	    ObjectInputStream in = new ObjectInputStream(oByteArrayInputStream);
	    inputEngine = (IREngine)in.readObject();
	    return inputEngine;
	}
	public ArrayList<WikiResult> runQuery(LinkedList<WikiDoc> wikiDocList, String term)
	{
		WikiResult wikiRes;
		ArrayList<WikiResult> resultArray = new ArrayList<>();
		double categoryIdf = computeInverseDocumentFrequencyCategory(wikiDocList, term);
		HashMap<Integer, Double> frequencyMapCategory = computeTermFrequencyCategory(term);
		for (Integer key : frequencyMapCategory.keySet()) {
			  double value = frequencyMapCategory.get(key);
			  value *= categoryIdf;
			  frequencyMapCategory.put(key, value);
			}
		
		double sectionIdf = computeInverseDocumentFrequencySection(wikiDocList, term);		
		HashMap<Integer, Double> frequencyMapSection = computeTermFrequencySection(term);
		for (Integer key : frequencyMapSection.keySet()) {
			  double value = frequencyMapSection.get(key);
			  value *= sectionIdf;
			  frequencyMapSection.put(key, value);
			}
		
		frequencyMapSection.forEach((k, v) -> frequencyMapCategory.merge(k, v, Double::sum));
		for (Entry<Integer, Double> entry : frequencyMapCategory.entrySet()) 
		{  
			wikiRes = new WikiResult();
			wikiRes.wiki = documentDictionary.get(entry.getKey()); ////int id = entry.getKey();
            wikiRes.score = entry.getValue(); // double tf = entry.getValue();
            resultArray.add(wikiRes);        
		}		
		return resultArray;
	}

	private int addDocumentToDictionary(WikiDoc doc)
	{
		int docId = documentDictionary.size();
		documentDictionary.put(docId, doc);
		return docId;
	}
	
	public void addTokenStream(TokenStream tokenStream)
	{
		int docId = addDocumentToDictionary(tokenStream.doc);
		addTokenStreamByType(tokenStream, TOKEN_TYPE.CATEGORY, docId);
		addTokenStreamByType(tokenStream, TOKEN_TYPE.SECTION, docId);
	}
	
	private void addTokenStreamByType(TokenStream tokenStream, TOKEN_TYPE type, int docId)
	{
		HashMap<String, Integer> termFrequency = tokenStream.getTokenOccurenceMap(type);
		int noOfTerms = termFrequency.size();
		for(Entry<String, Integer> term: termFrequency.entrySet())
		{
			int termId = termDictionaryByType.get(type).addTerm(term.getKey());
			postingsListByType.get(type).addPosting(docId, termId, term.getValue(), noOfTerms);
		}
	}	
	private double computeInverseDocumentFrequencyCategory(LinkedList<WikiDoc> wikiDocList, String term)
	{
		double size = computeNoOfDocsByType(TOKEN_TYPE.CATEGORY, term);
		if(size!=0.0)
			return Math.log(documentDictionary.size()/size);
		else
			return 0.0;
	}
	private double computeInverseDocumentFrequencySection(LinkedList<WikiDoc> wikiDocList, String term)
	{
		double size = computeNoOfDocsByType(TOKEN_TYPE.SECTION, term);
		if(size!=0.0)
			return Math.log(wikiDocList.size()/size);
		else
			return 0.0;
	}
	
	private double computeNoOfDocsByType(TOKEN_TYPE type, String term)
	{
		int termId = termDictionaryByType.get(type).getTermId(term);
		PostingsList postings = postingsListByType.get(type);
		double size = 0.0;
		if(termId!= -1)
		{
		LinkedList<PostingsListOccurence> postingsListNew = postings.postingsList.get(termId);
		size = postingsListNew.size();
		//System.out.println("size: "+size); DONT BE ANGRY , HAVE JUST KEPT THESE 3 STMNTS
		}
		return size;
	}
	
	private HashMap<Integer, Double> computeTermFrequencyCategory(String term)
	{
		int termId = termDictionaryByType.get(TOKEN_TYPE.CATEGORY).getTermId(term);
		PostingsList postings = postingsListByType.get(TOKEN_TYPE.CATEGORY);
		HashMap<Integer, Double> frequencyMap = new HashMap<>();
		LinkedList<PostingsListOccurence> postingsListNew = postings.postingsList.get(termId);
		if(termId!= -1)
		{
			for(PostingsListOccurence postingOccurence: postingsListNew)
			{
				//System.out.println("occurence "+postingOccurence.termOccurence);  DONT BE ANGRY , HAVE JUST KEPT THESE 3 STMNTS
				//System.out.println("noOfTerms "+postingOccurence.noOfTerms);  DONT BE ANGRY , HAVE JUST KEPT THESE 3 STMNTS
				double termFrequency = postingOccurence.termOccurence/ postingOccurence.noOfTerms;
				frequencyMap.put(postingOccurence.docId, termFrequency);			
			}
		}
		else
			frequencyMap.put(0, 0.0);
		return frequencyMap;
	}	
	
	private HashMap<Integer, Double> computeTermFrequencySection(String term)
	{
		int termId = termDictionaryByType.get(TOKEN_TYPE.SECTION).getTermId(term);
		PostingsList postings = postingsListByType.get(TOKEN_TYPE.SECTION);
		HashMap<Integer, Double> frequencyMap = new HashMap<>();
		LinkedList<PostingsListOccurence> postingsListNew = postings.postingsList.get(termId);
		if(termId!= -1)
		{
			for(PostingsListOccurence postingOccurence: postingsListNew)
			{
				//System.out.println("occurence "+postingOccurence.termOccurence); DONT BE ANGRY , HAVE JUST KEPT THESE 3 STMNTS
				//System.out.println("noOfTerms "+postingOccurence.noOfTerms); DONT BE ANGRY , HAVE JUST KEPT THESE 3 STMNTS
				double termFrequency = postingOccurence.termOccurence/ postingOccurence.noOfTerms;
				frequencyMap.put(postingOccurence.docId, termFrequency);			
			}
		}
		else
			frequencyMap.put(0, 0.0);
		return frequencyMap;
	}	
}

class TermDictionary implements Serializable
{
	public HashMap<String, Integer> termDictionary;
	
	public TermDictionary()
	{
		termDictionary = new HashMap<>();
	}
	public int addTerm(String term)
	{
		if(termDictionary.containsKey(term))
		return termDictionary.get(term);
		int termId = termDictionary.size();
		termDictionary.put(term, termId);
		return termId;
	}
	
	public int getTermId(String term){
		if(termDictionary.containsKey(term))
			return termDictionary.get(term);
		return -1;
	}
}

class PostingsList implements Serializable
{
	public HashMap<Integer, LinkedList<PostingsListOccurence>> postingsList;//changed from private to public
	
	public PostingsList()
	{
		postingsList = new HashMap<>();
	}	
	public void addPosting(int docId, int termId, int termOccurence, int noOfTerms)
	{
		PostingsListOccurence postingOccurence = new PostingsListOccurence(docId, termOccurence, noOfTerms);
		LinkedList<PostingsListOccurence> currentPostings;
		
		if(postingsList.containsKey(termId))
			currentPostings = postingsList.get(termId);
		else
		{
			currentPostings = new LinkedList<>();
			postingsList.put(termId, currentPostings);
		}	
		currentPostings.add(postingOccurence);
	}
		
}

class PostingsListOccurence implements Serializable
{
	public int docId;
	public double termOccurence;
	public double noOfTerms;
	
	public PostingsListOccurence(int docId, double termOccurence, double noOfTerms)
	{
		this.docId = docId;
		this.termOccurence = termOccurence;
		this.noOfTerms = noOfTerms;
	}
}	