package edu.ncsu.csc.nl.model.distance;

import edu.ncsu.csc.nl.model.Sentence;

/**
 * Computes the distance between two Strings based upon a simple edit distance
 * 
 * @author John
 *
 */
public class LevenshteinSentenceAsStringDistance extends SentenceDistance {
	
	/**
	 * Computes the Levenshtein Distance between two strings.  This distance, also referred to as the 
	 * edit distance, is the number of deletions, insertions, or substitutions required to transform s into t.
	 * 
	 * @param s
	 * @param t
	 * @return
	 */
	public double computeDistance (Sentence s, Sentence t) {
		return StringDistance.computeLevenshteinDistance(s.getSentence(), t.getSentence());
	}
	
	/**
	 * Returns "edit distance via string"
	 */
	public String getMethodName() {
		return "edit distance via string";
	}	
}
