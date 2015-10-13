package edu.ncsu.csc.nl.model.english;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
//import edu.ncsu.csc.nl.model.DomainDictionary;
//import edu.ncsu.csc.nl.model.DomainDictionaryEntry;
import edu.ncsu.csc.nl.model.WordEdge;
import edu.ncsu.csc.nl.model.WordVertex;
import edu.ncsu.csc.nl.model.type.PartOfSpeech;
import edu.ncsu.csc.nl.model.type.Relationship;


/**
 * The class represents the various ways we attempt to find negation
 * 
 * 
 * @author John
 *
 */
public class Negation {

	public static String[] negativePrefixes = {"a","an","anti","dis","in","il","im","ir","non","un"}; // source: Cambridge Grammar, p 1687
	
	public static String[] negativeDeterminers = {"no","zero","neither"};  //TODO: Validate this list from the cambridge book
	
	public static String[] negativeAdjectives = {"unable"};
	
	public static String[] negativeNouns = {"none","nothing"};

	public static String[] negativeAdverbs = {"never","nowhere","no","not"}; //source: http://www.elearnenglishlanguage.com/blog/learn-english/grammar/negative-adverbs/
	
	public static String[] exceptedVerbsFromNegativePrefixes = {"display"};
	
	/**
	 * Test whether or not the passed in word is negative or not based upon the prefix
	 * To do this, we first test if the word begins with a prefix, if it does, is the remainder of the word a valid word?
	 * 
	 * @param word
	 * @param pos
	 */
	public static boolean isWordNegativeFromPrefix(Dictionary dict, String word, POS pos) {

		for (String s: exceptedVerbsFromNegativePrefixes) {
			if (s.equalsIgnoreCase(word)) {
				return false;
			}
		}
		
		for (String prefix: negativePrefixes) {
			if (word.startsWith(prefix)) {
				String remainder = word.substring(prefix.length());
				
				if (dict.getIndexWord(remainder,pos) != null) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Test for negation in this node through the following methods:
	 * 1) Is it the word vertex the child of a negation modifier / relationship: this is typically "not" or "conj_negcc"
	 * 2) Is the word a negative determiner?
	 * 3) Is the word a negative noun?
	 * 4) Is the word a negative adjective(unable)
	 * 5) Is the word a negative adverb
	 * 6) Is the word a negative verb?
	 *    a) in our dictionary as negative?
	 *    b) has a negative prefix?
	 * 
	 * @param dict can be passed as null
	 * @param wv
	 * @return
	 */
	public static boolean isWordVertexNegative(Dictionary dict, WordVertex wv) {
		String lemma = wv.getLemma();
		
		// 1
		for (int i=0;i < wv.getNumberOfParents();i++) {
			WordEdge edgeToParent = wv.getParentAt(i);
			if (edgeToParent.getRelationship() == Relationship.NEG || edgeToParent.getRelationship() == Relationship.CONJ_NEGCC) {
				return true;
			}
		}
		
		//2
		if (wv.getPartOfSpeech()==PartOfSpeech.DT) {
			
		    for (String word:negativeDeterminers) {
		    	if (lemma.equals(word)) {
		    		return true;
		    	}
		    }
		}
		
		//3
		if (wv.getPartOfSpeech().getCollapsedPOS() ==PartOfSpeech.NN) {
		    for (String word:negativeNouns) {
		    	if (lemma.equals(word)) {
		    		return true;
		    	}
		    }
		}
		
		
		//4
		if (wv.getPartOfSpeech().getCollapsedPOS() ==PartOfSpeech.JJ) {
		    for (String word:negativeAdjectives) {
		    	if (lemma.equals(word)) {
		    		return true;
		    	}
		    }
		}		

		//5
		if (wv.getPartOfSpeech().getCollapsedPOS() == PartOfSpeech.RB) {
		    for (String word:negativeAdverbs) {
		    	if (lemma.equals(word)) {
		    		return true;
		    	}
		    }
		}		
		
		
		/*
		//6a
		if (domainDictionary != null && wv.getPartOfSpeech().getCollapsedPOS() == PartOfSpeech.VB) {
			DomainDictionaryEntry dde = domainDictionary.getEntry(lemma, DomainFlag.ACTION);
			if (dde != null && dde.isNegative()) {
				return true;
			}
		}
		*/
		//6a hack
		String negativeVerbs[] = {"prohibit","stop","prevent" };
		if (wv.getPartOfSpeech().getCollapsedPOS() == PartOfSpeech.VB)  {
			for (String verb: negativeVerbs) {
				if (wv.getLemma().equals(verb)) {
					return true;
				}
			}
		}
		
		//6b 
		if (dict != null  && wv.getPartOfSpeech().getCollapsedPOS() == PartOfSpeech.VB) {
			boolean tempResult = isWordNegativeFromPrefix(dict,lemma,POS.VERB);
			if (tempResult) { return true; }
		}
		
		return false;
	}
	
}
