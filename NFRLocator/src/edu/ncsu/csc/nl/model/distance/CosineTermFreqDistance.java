package edu.ncsu.csc.nl.model.distance;

import java.util.HashMap;
import java.util.HashSet;

import edu.ncsu.csc.nl.model.Sentence;


/**
 * Computes the cosine distance between two sentences.
 * Note: we invert the normal result, so that 0 is closest and 1 is furtherst 
 *       to match the semantics of the other distance metrics. 
 * 
 * sim(S,T) = 1 - (sum S_i * T_i)/ (sqrt (sum(S_i*S_i)) * sqrt(sum(T_i*T_i)))
 * 
 * 
 * @author John
 *
 */
public class CosineTermFreqDistance extends SentenceDistance {
	
	/**
	 * 
	 * 
	 */
	public double computeDistance (Sentence s, Sentence t) {
		HashMap<String,Integer> frequencyS = s.getFrequencyMap();
		HashMap<String,Integer> frequencyT = t.getFrequencyMap();
		
		
		HashSet<String> words = new HashSet<String>(frequencyS.keySet());
		words.addAll(frequencyT.keySet());
		
		int numerator = 0;
		for (String key: words) {
			int s_i = frequencyS.containsKey(key) ? frequencyS.get(key): 0;
			int t_i = frequencyT.containsKey(key) ? frequencyT.get(key): 0;
			numerator += (s_i * t_i);
		}
		
		double denominator = s.computeFrequencyVectorLength() * t.computeFrequencyVectorLength();
		
		return  1.0 - (numerator/denominator);
	}
		
	/**
	 * Returns "TDF - cosine"
	 */
	public String getMethodName() {
		return "TDF - cosine";
	}	
}
