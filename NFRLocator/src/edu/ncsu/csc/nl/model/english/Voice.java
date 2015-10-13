package edu.ncsu.csc.nl.model.english;

import java.util.HashSet;

import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.WordVertex;
import edu.ncsu.csc.nl.model.type.Relationship;

/**
 * Container class for logic around active vs. passive voice
 * 
 * @author John
 *
 */
public class Voice {

	
	/**
	 * A sentence is considered passive if any of these relationships are present: agent, auxpass, csubjpass, nsubjpass
	 * 
	 * @param s
	 * @return
	 */
	public static boolean inPassiveVoice(Sentence s) {
		for (int i=0; i<s.getNumberOfNodes();i++) {
			WordVertex w = s.getWordVertexAt(i);
			for (int j=0; j< w.getNumberOfParents();j++) {
				Relationship r = w.getParentAt(j).getRelationship();
				if (r == Relationship.AUXPASS || r == Relationship.AGENT || r== Relationship.CSUBJPASS || r==Relationship.NSUBJPASS) {
					return true;
				}
 			}
		}
		return false;
	}
	
	/**
	 * A sentence is considered passive if any of these relationships are present: agent, auxpass, csubjpass, nsubjpass
	 * 
	 * @param s
	 * @return
	 */
	public static boolean inPassiveVoice(WordVertex vw) {
		return inPassiveVoice(vw, new HashSet<Integer>());
	}	
	
	private static boolean inPassiveVoice(WordVertex vw, HashSet<Integer> visitedNodes) {
		if ( visitedNodes.contains(vw.getID())) {	return false; }
		else {	visitedNodes.add(vw.getID());}		
		
		for (int j=0; j< vw.getNumberOfChildren();j++) {
			Relationship r = vw.getChildAt(j).getRelationship();
			if (r == Relationship.AUXPASS || r == Relationship.AGENT || r== Relationship.CSUBJPASS || r==Relationship.NSUBJPASS) {
				return true;
			}
		}
		for (int j=0; j< vw.getNumberOfChildren();j++) {
			if (Voice.inPassiveVoice(vw.getChildAt(j).getChildNode(),visitedNodes)) {
				return true;
			}
		}
		return false;
	}
}
