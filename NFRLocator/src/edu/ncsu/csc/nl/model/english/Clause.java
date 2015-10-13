package edu.ncsu.csc.nl.model.english;

import java.util.HashSet;

import edu.ncsu.csc.nl.model.WordVertex;
import edu.ncsu.csc.nl.model.type.Relationship;

public class Clause {
	
	/**
	 * A sentence is considered passive if any of these relationships are present: agent, auxpass, csubjpass, nsubjpass
	 * 
	 * @param s
	 * @return
	 */
	public static boolean hasClause(WordVertex vw) {
		return hasClause(vw, new HashSet<Integer>());
	}	
	
	private static boolean hasClause(WordVertex vw, HashSet<Integer> visitedNodes) {
		if ( visitedNodes.contains(vw.getID())) {	return false; }
		else {	visitedNodes.add(vw.getID());}		
		
		for (int j=0; j< vw.getNumberOfChildren();j++) {
			Relationship r = vw.getChildAt(j).getRelationship();
			if (r == Relationship.ADVCL) {
				return true;
			}
		}
		for (int j=0; j< vw.getNumberOfChildren();j++) {
			if (Clause.hasClause(vw.getChildAt(j).getChildNode(),visitedNodes)) {
				return true;
			}
		}
		return false;
	}	

}
