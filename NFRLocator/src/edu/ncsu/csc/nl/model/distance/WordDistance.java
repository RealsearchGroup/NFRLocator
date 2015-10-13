package edu.ncsu.csc.nl.model.distance;

import java.util.HashMap;

import edu.ncsu.csc.nl.model.WordNetSynonymSet;
import edu.ncsu.csc.nl.model.WordVertex;
import edu.ncsu.csc.nl.model.type.PartOfSpeech;

public class WordDistance {

	private static HashMap<String, java.util.ArrayList<WordNetSynonymSet>> relatedWordNetSets = new HashMap<String, java.util.ArrayList<WordNetSynonymSet>>();
	
	private static String makeKey(WordVertex a) {
		return a.getLemma()+"-"+a.getPartOfSpeech().getWordNetPOS();
	}
	
	
	private static java.util.ArrayList<WordNetSynonymSet> getRelatedWordSet(WordVertex a) {
		//System.out.println(relatedWordNetSets.size()+" "+a.getLemma());
		String key = makeKey(a);
		java.util.ArrayList<WordNetSynonymSet> result = relatedWordNetSets.get(key);
		if (result == null) { // we didn't find it in our cache

			result = new java.util.ArrayList<WordNetSynonymSet>();
			relatedWordNetSets.put(key,result);
		}
		return result;
	}
	
	
	
	/** 
	 * The distance between two nodes is defined as
	 *    (note because of how we walk the vertices, the nodes will always have the same indexes, so don't check those)
	 *  - if the nodes have a different POS, 1
	 *    if the nodes have different parents, 1  (this implies a different structure).  Note: With the graph walk, we will work around this issue...
	 */
	public static double getNodeDistance(WordVertex a, WordVertex b) {
		//double distance = 0.0;
		
		if (a== null && b==null) { 
			System.out.println("Two null nodes passed in to getnodedistance");
			return 0;
		}
		if (a == null || b== null) {
			return 1.0;
		}
		if (a.getLemma().equalsIgnoreCase(b.getLemma())) {   
			return 0.0;
		}

		if (a.getPartOfSpeech().equalsCollapsed(b.getPartOfSpeech()) == false) {
			return 1.0;
		}
		
		//test parent relationships
		int aParentCount = a.getNumberOfParents();
		int bParentCount = b.getNumberOfParents();
		
		if (aParentCount != bParentCount){
			return 1.0;
		}		
		for (int i=0;i<aParentCount;i++) {
			boolean found = false;
			for (int j=0;j<bParentCount;j++) {
				if (a.getParentAt(i).getRelationship() == b.getParentAt(j).getRelationship()) {
					found = true;
				}
			}
			if (!found) {
				return 1.0;
			}
		}
		
		//consider all numbers to be the same
		if (a.getPartOfSpeech() == PartOfSpeech.CD) {
			return 0.0;
		}
		
		// test the lemma.  not: if the lemmas are equal, override any other value and return 0;
		// we've tested the part of speech already so we know its the same.
		if (a.getLemma().equalsIgnoreCase(b.getLemma())) {   
			return 0.0;
		}		
		
		if (!a.getNamedEntityRecognizedLabel().equals("") && a.getNamedEntityRecognizedLabel().equals(b.getNamedEntityRecognizedLabel())) {
			//System.out.println(a.getNamedEntityRecognizedLabel());
			return 0.0;
		}	
		/*
		// add check for wordNet Synonym  (.1,.2,.4)
		java.util.ArrayList<ClassificationAttributeJUNKTRASH> words = getRelatedWordSet(a);
		if (words != null) {
			for (ClassificationAttributeJUNKTRASH erss: words) {
				if (erss.containsWord(b.getLemma())) {
					//System.out.println(a.getLemma()+":"+b.getLemma()+":"+erss.getDistance());
					if (erss.getDistance() == 1) { return 0.1; }
					if (erss.getDistance() == 2) { return 0.2; }
					if (erss.getDistance() == 3) { return 0.4; }
				}
			}
		}
		*/
		// if we get to the bottom, return the default of .75
		
		return 1;  // comparing 0.25, .5, .75 and 1, this appears to be the best...
		
	}
	
	

}
