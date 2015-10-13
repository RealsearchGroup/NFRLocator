package edu.ncsu.csc.nl.playground;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;

import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.ncsu.csc.nl.model.PorterStemmer;

public class WordNetSampleCode {

	public static void printWordStems(Dictionary dict, WordnetStemmer ws, String word, POS pos ) {
		System.out.println("Finding word stems for "+word+", with POS tag:"+pos);
		
		List<String> l = ws.findStems(word,pos);
		for(String s: l) {
			System.out.print(" \t"+s+" ");
			
			IIndexWord idxWord = dict.getIndexWord(s,pos);
			if (idxWord != null) {
				IWordID wordID = idxWord.getWordIDs ().get (0) ;
				IWord iword = dict.getWord( wordID );			
				System.out.println(iword);
			}
			else {
				System.out.println("stem not found in dictionary");
			}
		}
	}
	public static void printRelatedWords(Dictionary dict, ISynset synset, IPointer ptr, String label) {
		List<ISynsetID> hypernyms =  synset.getRelatedSynsets(ptr);
		if (hypernyms.size() ==0) {
			System.out.println("\t\t"+label+": none");
		}
		for(ISynsetID isid: hypernyms) {
			ISynset is = dict.getSynset(isid);
			System.out.println("\t\t"+label+":"+   is.getGloss());
			
			System.out.print("\t\t\t");
			List<IWord> iwords = is.getWords();
			
			for(Iterator <IWord > i = iwords.iterator(); i.hasNext();) {
				System.out.print(i.next().getLemma());
				if (i.hasNext()) {
					System.out.print(", ");
				}
			}
			System.out.println();
			
			/* this code is basically the same as above, which just gets around the fencepost problem
			for (IWord iw: iwords) {
				System.out.print(iw.getLemma()+", ");
			}
			System.out.println("");
			*/
		}		
	}
	
	
	//note: need to pass in the stem of the word
	public static void printRelations(Dictionary dict, String word, POS pos) {
		System.out.println("Analyzing relations: "+word);
		IIndexWord idxWord = dict.getIndexWord(word,pos);
		if (idxWord == null) {
			System.out.println("\tUnable to find a verb for "+word);
			return;
		}
		
		List<IWordID> ids = idxWord.getWordIDs(); //get all of the different entries
		for (IWordID wordID: ids) {
			IWord iword = dict.getWord( wordID ); // look at one specific entry
			ISynset synset = iword.getSynset();
			
			System.out.println("\t" + iword.getLemma()+"(" + wordID+"/"+synset.getID()+"): "+ synset.getGloss ());
			
			//List the words in this synset
			System.out.print("\t\tWords: ");
			List<IWord> words =  synset.getWords();
			for (int i=0;i<words.size();i++) {
				if (i>0) { System.out.print(", "); }
				System.out.print(words.get(i).getLemma());
				
				//To follow lexical pointers, can use get w.getRelatedWords();
			}
			System.out.println();
			
			//This code shows how to list the available semantic pointers for a given synset
			System.out.print("\t\tRelations: ");
			Map<IPointer, List<ISynsetID>> map = synset.getRelatedMap();     //each semantic type can link to multiple semantic groups - in the list
			for (IPointer ip: map.keySet()){
				System.out.print(ip+", ");
			}
			System.out.println();
			for (IPointer ip: map.keySet()){
				printRelatedWords(dict, synset, ip, ip.getName());
			}		
			
			/*
			printRelatedWords(dict, synset, Pointer.HYPERNYM, "Hypernym");       //less specific
			printRelatedWords(dict, synset, Pointer.HYPONYM, "Hyponym");         //more detailed
			printRelatedWords(dict, synset, Pointer.ENTAILMENT, "Entailment");   //implies
			printRelatedWords(dict, synset, Pointer.ANTONYM, "Antonym");
			printRelatedWords(dict, synset, Pointer.DERIVATIONALLY_RELATED, "Derived");
			printRelatedWords(dict, synset, Pointer.VERB_GROUP, "Verb Group");
			*/			
		}		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			java.io.File wordnetDir = new java.io.File("C:/NLP/WordNetDictionary");
			Dictionary dict = new Dictionary(wordnetDir);
			dict.open();
			//dict.load(true);
			
			//Find Stems of a word
			//Note: I wasn't very impressed with these results.  See their javadocs. Does simple POS suffic ending replacement
			//While the API was writing at MIT, they appear to be following the guidance from the WordNet team in their 
			//implementation.  Also, I Stanford's parser does stemming too.  That parser should have an advantage
			//as it can use parts of speech and sentence semantics to guide it.
			System.out.println("-------------------------------------");
			System.out.println("---WordNET Stemming---");
			WordnetStemmer ws = new WordnetStemmer(dict);
			printWordStems(dict, ws, "running",POS.VERB);
			
			System.out.println("-------------------------------------");
			System.out.println("---Porter Stemming---");
			System.out.println("running" +":"+ PorterStemmer.getStem("running"));
			System.out.println("ran" +":"+ PorterStemmer.getStem("running"));
			System.out.println("travelled" +":"+ PorterStemmer.getStem("travelled"));
			System.out.println("cement" +":"+ PorterStemmer.getStem("cement"));
			System.out.println("university" +":"+ PorterStemmer.getStem("university"));
			System.out.println("universe" +":"+ PorterStemmer.getStem("universe"));			
//System.exit(0);			
			System.out.println("-------------------------------------");

			printRelations(dict, "create",POS.VERB);
			/*
			printRelations(dict, "cancel",POS.VERB);
			printRelations(dict, "update",POS.NOUN);
			printRelations(dict, "update",POS.VERB);
			printRelations(dict, "dead",POS.ADJECTIVE);
			printRelations(dict, "retrieve",POS.VERB);
			printRelations(dict, "delete",POS.VERB);
			printRelations(dict, "insert",POS.VERB);
			printRelations(dict, "select",POS.VERB);
			printRelations(dict, "prevent",POS.VERB);
			printRelations(dict, "disallow",POS.VERB);
			printRelations(dict, "stop",POS.VERB);
			printRelations(dict, "halt",POS.VERB);			
			//printRelations(dict, "doctor",POS.NOUN);
			*/
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isCountry(IWord word, IDictionary dict) {
		//System.out.println("Examining: "+word.getLemma()+"("+word+")");

		if (word.getLemma().equalsIgnoreCase("country")) {
			return true;
		}
		
		//if (depth > 20) {return false; }
		
		ISynset synset = word.getSynset();
		java.util.List<ISynsetID> relatedSynsets = synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE);
		for (ISynsetID sid: relatedSynsets) {
			java.util.List<IWord> words = dict.getSynset(sid).getWords();
			for (IWord w: words) {
				boolean result = isCountry(w,dict);
				if (result) {
					return true;
				}
			}
		}
		
		relatedSynsets = synset.getRelatedSynsets(Pointer.HYPERNYM);
		for (ISynsetID sid: relatedSynsets) {
			java.util.List<IWord> words = dict.getSynset(sid).getWords();
			for (IWord w: words) {
				boolean result = isCountry(w,dict);
				if (result) {
					return true;
				}
			}
		}		
				
		return false;
	}	
	
	
	public static boolean isCountry(IWord word, IDictionary dict, java.util.List<IWord> followedWords, java.util.List<IWord> toExamineWords, int depth) {
		//System.out.println("Examining: "+word.getLemma()+"("+word+")");
		if (!followedWords.contains(word)) { followedWords.add(word); }
		if (word.getLemma().equalsIgnoreCase("country")) {
			return true;
		}
		
		if (depth > 20) {return false; }
		
		ISynset synset = word.getSynset();
		java.util.List<ISynsetID> relatedSynsets = synset.getRelatedSynsets();
		for (ISynsetID sid: relatedSynsets) {
			java.util.List<IWord> words = dict.getSynset(sid).getWords();
			for (IWord w: words) {
				if (followedWords.contains(w)== false) {
					toExamineWords.add(w);
				}
			}
		}
		
		while (toExamineWords.isEmpty() == false) {
			IWord w = toExamineWords.remove(0);
			if (followedWords.contains(w)) {
				continue;
			}
			boolean result = isCountry(w,dict, followedWords, toExamineWords, depth+1);
			if (result) {
				return true;
			}
		}			
				
		return false;
	}

}
