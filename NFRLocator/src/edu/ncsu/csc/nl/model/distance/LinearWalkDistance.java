package edu.ncsu.csc.nl.model.distance;

import edu.ncsu.csc.nl.model.Sentence;

/**
 * Computes the distance by walking the nodes in order. 
 * 
 * @author John
 *
 */
public class LinearWalkDistance extends SentenceDistance {
	
	/**
	 * Returns a distance reflecting how far apart 2 sentences are
	 * Greater value implies greater difference.  Smaller values = more similar
	 * 
	 * @param s
	 * @param t
	 * @return distance between two sentences.  0 implies an exact match.
	 */
	public double computeDistance (Sentence s, Sentence t) {
		int numberOfNodesToCheck = Math.min(s.getNumberOfNodes(), t.getNumberOfNodes());
		int diffenceInNumberOfNodes = Math.abs(s.getNumberOfNodes() - t.getNumberOfNodes());
		
		
		double distance = 0.0;
		
		distance += diffenceInNumberOfNodes;
		
		for (int i=0;i<numberOfNodesToCheck;i++) {
			distance += WordDistance.getNodeDistance(s.getWordVertexAt(i), t.getWordVertexAt(i));
		}
		
		return distance;
	}
	
	/**
	 * Returns "linear walk"
	 */
	public String getMethodName() {
		return "linear walk";
	}
}
