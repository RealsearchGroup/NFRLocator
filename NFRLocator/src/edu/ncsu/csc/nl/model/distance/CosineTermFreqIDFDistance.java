package edu.ncsu.csc.nl.model.distance;

import java.util.HashMap;
import java.util.HashSet;

import edu.ncsu.csc.nl.model.Sentence;



/**
 * Computes the cosine distance between two sentences.
 * Note: we invert the normal result, so that 0 is closest and 1 is furtherst 
 *       to match the semantics of the other distance metrics. 
 * 
 * sim(S,T) = 1 - (sum S_i * T_i * IDF_i*IDF_i)/ (sqrt (sum(S_i*S_i)) * sqrt(sum(T_i*T_i)))
 * 
 * 
 * @author John
 *
 */
public class CosineTermFreqIDFDistance extends SentenceDistance {
	
	HashMap<String, Double> _inverseDocumentFrequency;
	int _numberOfSentences;
	double _defaultIDF;
	
	public void setInverseDocumentFrequncy( HashMap<String, Double> inverseDocumentFrequency) {
		_inverseDocumentFrequency = inverseDocumentFrequency;
	}
	
	public void setNumberOfSentences(int newValue) {
		_numberOfSentences = newValue;
		_defaultIDF = Math.log(_numberOfSentences);
		_defaultIDF = 1.0;
	}
	
	
	/**
	 * 
	 * 
	 */
	public double computeDistance (Sentence s, Sentence t) {
		HashMap<String,Double> frequencyS = s.getFrequencyMapNormalized();
		HashMap<String,Double> frequencyT = t.getFrequencyMapNormalized();
		
		
		HashSet<String> words = new HashSet<String>(frequencyS.keySet());
		words.addAll(frequencyT.keySet());
		
		//Let's play with stop words
		//StopWord sw = new StopWord();
		//for (String stopWord: sw.frakes) {
		//	words.remove(stopWord);
		//}
		
		
		double numerator = 0;
		for (String key: words) {
			double s_i = frequencyS.containsKey(key) ? frequencyS.get(key): 0.0;
			double t_i = frequencyT.containsKey(key) ? frequencyT.get(key): 0.0;
			double idf = _inverseDocumentFrequency.containsKey(key) ? _inverseDocumentFrequency.get(key) : _defaultIDF;
			numerator += (s_i * t_i * idf * idf);
		}
		
		double denominator = s.computeFrequencyVectorLength(_inverseDocumentFrequency,_defaultIDF) * t.computeFrequencyVectorLength(_inverseDocumentFrequency,_defaultIDF);
		
		return  1.0 - (numerator/denominator);
	}
		
	/**
	 * Returns "TDF - cosine"
	 */
	public String getMethodName() {
		return "TDF IDF - cosine";
	}	
}
