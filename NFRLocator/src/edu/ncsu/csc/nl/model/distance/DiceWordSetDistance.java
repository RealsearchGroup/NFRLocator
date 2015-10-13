package edu.ncsu.csc.nl.model.distance;

import java.util.HashSet;


import edu.ncsu.csc.nl.model.Sentence;


/**
 * Dice Word Set Distance =  2* | W_s  intersect  W_t | /   ( |W_s| +|W_t|  )
 * 
 * 
 * @author John
 *
 */
public class DiceWordSetDistance extends SentenceDistance {

	
	public  double computeDistance (Sentence s, Sentence t) {
		HashSet<String> setS = new HashSet<String>();
		
		for (int i=0;i<s.getNumberOfNodes();i++) {
			setS.add(s.getWordVertexAt(i).getLemma());
		}
		
		HashSet<String> setT = new HashSet<String>();
		for (int i=0;i<t.getNumberOfNodes();i++) {
			setT.add(t.getWordVertexAt(i).getLemma());
		}
		
		double denominator = setS.size() + setT.size();
		
		setS.retainAll(setT); // this is now the intersection;
		
		return  1.0 - (setS.size()/denominator);

	}
		
	/**
	 * Returns "word set - Dice"
	 */
	public String getMethodName() {
		return "word set - Dice";
	}		
}
