package edu.ncsu.csc.nl.model.distance;

import java.util.HashSet;

import edu.ncsu.csc.nl.model.Sentence;


/**
 * Jacard Distance   | W_s  intersect  W_t | / |W_s union W_t|
 * 
 * @author John
 *
 */
public class JacardWordSetDistance extends SentenceDistance {
	
	public double computeDistance (Sentence s, Sentence t) {
		HashSet<String> setS = new HashSet<String>();
		
		for (int i=0;i<s.getNumberOfNodes();i++) {
			setS.add(s.getWordVertexAt(i).getLemma());
		}
		
		HashSet<String> setT = new HashSet<String>();
		for (int i=0;i<t.getNumberOfNodes();i++) {
			setT.add(t.getWordVertexAt(i).getLemma());
		}
		
		HashSet<String> intersection = new HashSet<String>(setS);
		intersection.retainAll(setT);
		
		setT.addAll(setS); // setT is now the union;
		
		//System.out.println(intersection);
		
		return  1.0 -(  ( (double) intersection.size())/setT.size());
	}
		
	/**
	 * Returns "word set - Jacard"
	 */
	public String getMethodName() {
		return "word set - Jacard";
	}	
}
