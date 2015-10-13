package edu.ncsu.csc.nl.model.english;

import java.util.HashSet;
import java.util.List;

import edu.ncsu.csc.nl.model.WordVertex;
import edu.ncsu.csc.nl.model.type.PartOfSpeech;

public class Ambiguity {

	public static final String AMBIGUOUS_WORDS[] = {"alternate", "any", "chart", "change","copy","date", "datum", "detail", "entry", "form","function","history", "list", "listing", 
		                                   "information", "item","measurement","menu", "message", "name", "none", 
	                                       "number","one", "option", "overview", "pair", "queue", "reading","record", "report", "request", "row", "section","system", "that", "type", "user", "value" };
	
	public static final String WORDS_NOT_ALLOWABLE_IN_SOA[] = {"use case","be","have","this"};
	
	private static HashSet<String> _notInSOAWordList = new HashSet<String>();
	private static HashSet<String> _ambiguousWordList = new HashSet<String>();
	static {
		for (String word: AMBIGUOUS_WORDS) { _notInSOAWordList.add(word); _ambiguousWordList.add(word); }
		for (String word: WORDS_NOT_ALLOWABLE_IN_SOA) { _notInSOAWordList.add(word); }

	}
		
	public static boolean isAmbiguous(WordVertex wv) {
		return isAmbiguous(wv.getPartOfSpeech()) || isAmbiguous(wv.getLemma());
	}
	
	public static boolean isAmbiguous(List<WordVertex> wvList) {
		if (wvList.size() == 0) { return true; }
		if (wvList.size() > 1) { return false; }
		
		WordVertex wv = wvList.get(0);
		return isAmbiguous(wv.getPartOfSpeech()) || isAmbiguous(wv.getLemma());
	}	
	
	
	public static boolean isAmbiguous(PartOfSpeech pos) {
		return pos == PartOfSpeech.PRP || pos == PartOfSpeech.PRP$;
	}
	
	public static boolean isAmbiguous(String lemma) {
		return _ambiguousWordList.contains(lemma);
	}
	
	public static boolean allowableInSOA(String lemma) {
		return  lemma.equals("user") || lemma.equals("system") || (_notInSOAWordList.contains(lemma) == false);
	}
}
