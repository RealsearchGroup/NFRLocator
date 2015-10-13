package edu.ncsu.csc.nl.model.distance;

import edu.ncsu.csc.nl.model.Sentence;

/**
 * Computes the distance between two Strings based based upon their tree relationship strings: (VB root (NN nsubj )(NN dobj ))
 * The difference between the strings is computed as via the Levenstein distance.
 * 
 * @author John
 *
 */
public class TreeRelationshipAsLevenshteinDistance extends SentenceDistance {
	
	/**
	 * Computes the Levenshtein Distance between two strings.  This distance, also referred to as the 
	 * edit distance, is the number of deletions, insertions, or substitutions required to transform s into t.
	 * 
	 * @param s
	 * @param t
	 * @return
	 */
	public double computeDistance (Sentence s, Sentence t) {
		
		/*return  StringDistance.computeLevenshteinDistance(s.getRoot().getStringRepresentationUltraCollapsed(), 
                t.getRoot().getStringRepresentationUltraCollapsed());
		*/
		return (StringDistance.computeLevenshteinDistance(s.getRoot().getStringRepresentationUltraCollapsed(), 
				                                         t.getRoot().getStringRepresentationUltraCollapsed()) +
			    (new LevenshteinSentenceAsWordsDistance()).computeDistance(s, t))/2.0;
			    
	}
	
	/**
	 * Returns "edit distance via string"
	 */
	public String getMethodName() {
		return "edit distance via tree rleationship";
	}	
}
