package edu.ncsu.csc.nl.model.distance;

import java.util.ArrayList;
import java.util.HashSet;

import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.WordEdge;
import edu.ncsu.csc.nl.model.WordVertex;
import edu.ncsu.csc.nl.model.type.PartOfSpeech;

/**
 * Computes the distance by walking the nodes in order. 
 * 
 * @author John
 *
 */
public class GraphWalkDistance extends SentenceDistance {
	

	
	public  double computeDistance(Sentence a, Sentence b) {
		try {
			return (getSentenceDistanceViaGraphWalk(a.getRoot(), b.getRoot(), new HashSet<WordVertex>(), new HashSet<WordVertex>()));//   + Math.abs(a.getNumberOfNodes() - b.getNumberOfNodes())    );
		}
		catch(Throwable t) {
			System.err.println("Get Sentence distance: "+t);
			System.err.println("    "+a);
			System.err.println("    "+b);
			return Double.MAX_VALUE;
		}
	}
	
	
	private double getSentenceDistanceViaGraphWalk(WordVertex a, WordVertex b, 
														  HashSet<WordVertex> visitedWordsForSentenceA,
														  HashSet<WordVertex> visitedWordsForSentenceB) {
		
		
		// This was wrong ----> Check if we've already visited these two nodes.  If we have seen either one, return;
		// Correct logic,  if we've seen one, set it to null.
		if (visitedWordsForSentenceA.contains(a)) {
			a = null;
		}
		if (visitedWordsForSentenceB.contains(b)) {
			b = null;
		}
		
		//mark the nodes as visited
		if (a != null) {visitedWordsForSentenceA.add(a);}
		else {
			a = new WordVertex(-1, "No such word exists", "noWord",  PartOfSpeech.$,"O",-1,-1,-1);
		}
		if (b != null) {visitedWordsForSentenceB.add(b);}
		else {
			b = new WordVertex(-1, "No such word exists", "noWord",  PartOfSpeech.$,"O",-1,-1,-1);
		}
		
		double currentNodeDistance = WordDistance.getNodeDistance(a,b);
		int numberOfChildrenToCheck    = Math.min(a.getNumberOfChildren(), b.getNumberOfChildren());
		WordVertex smallerChildrenNode = a;
		WordVertex largerChildrenNode = b;
		if (a.getNumberOfChildren() > b.getNumberOfChildren()) {
			smallerChildrenNode = b;
			largerChildrenNode = a;
		}
		
		
		//int diffenceInNumberOfChildren = Math.abs(a.getNumberOfChildren() - b.getNumberOfChildren());	
		//currentNodeDistance += diffenceInNumberOfChildren; // missing nodes from one graph to another have a cost of 1 each.  CAN'T DO THIS.  NEED TO WALK THE NODE TO GET IT.
		
		//visit the children and get their costs
		//TODO: Get smarter about how this visitation works.  If there are same relationships of children we may want to follow those instead.
		ArrayList<WordEdge> duplicateLargerChildren = new ArrayList<WordEdge>();
		for (int i=0;i<largerChildrenNode.getNumberOfChildren();i++) {
			duplicateLargerChildren.add(largerChildrenNode.getChildAt(i));
		}
		
		for (int i=0; i < numberOfChildrenToCheck; i++) {
			WordVertex largerNodeToVisit = null;
			
			for (WordEdge we: duplicateLargerChildren) {
				if (we.getRelationship() == smallerChildrenNode.getChildAt(i).getRelationship()) {
					largerNodeToVisit = we.getChildNode();
					duplicateLargerChildren.remove(we);
					break;
				}
			}
			
			if (largerNodeToVisit == null) {
				largerNodeToVisit = duplicateLargerChildren.get(0).getChildNode();
				duplicateLargerChildren.remove(0);
			}
			
			if (a.getNumberOfChildren() > b.getNumberOfChildren()) {
				currentNodeDistance += getSentenceDistanceViaGraphWalk(largerNodeToVisit,smallerChildrenNode.getChildAt(i).getChildNode(), visitedWordsForSentenceA, visitedWordsForSentenceB);
			}
			else {
				currentNodeDistance += getSentenceDistanceViaGraphWalk(smallerChildrenNode.getChildAt(i).getChildNode(),largerNodeToVisit, visitedWordsForSentenceA, visitedWordsForSentenceB);
			}
		
			
		}
		

		//now visit of the larger nodes
		for (WordEdge we: duplicateLargerChildren) {
			if (a.getNumberOfChildren() > b.getNumberOfChildren()) {
				currentNodeDistance += getSentenceDistanceViaGraphWalk(we.getChildNode(),null, visitedWordsForSentenceA, visitedWordsForSentenceB);
			}
			else {
				currentNodeDistance += getSentenceDistanceViaGraphWalk(null,we.getChildNode(), visitedWordsForSentenceA, visitedWordsForSentenceB);
			}
		}
		
		return currentNodeDistance;
	}
	
	
	
	
	/**
	 * Returns "graph walk"
	 */
	public String getMethodName() {
		return "graph walk";
	}
	
	
}
