package edu.ncsu.csc.nl.model.distance;

import edu.ncsu.csc.nl.model.Sentence;
import edu.ncsu.csc.nl.model.type.SentenceType;

/**
 * Used to define behavior of classes that 
 * 
 * @author John
 *
 */
public abstract class SentenceDistance {
	
	/**
	 * Returns a distance reflecting how far apart 2 sentences are
	 * Greater value implies greater difference.  Smaller values = more similar
	 * 
	 * @param s
	 * @param t
	 * @return distance between two sentences.  0 implies an exact match.
	 */
	public abstract double computeDistance (Sentence s, Sentence t);
	
	
	/**
	 * Returns a name description for the particular method used to make sentences similar
	 * 
	 * @return method name
	 */
	public abstract String getMethodName();
	
	public double getSentenceOffsetDistance(Sentence s, Sentence t) {
		double result = 0.0;
		
		if (s.getSentenceType() == t.getSentenceType()) {
			return 0.0;
		}
		
		switch (s.getSentenceType()) {
			case UNKNOWN:   return 10.0;
			case NORMAL: 	if (t.getSentenceType() == SentenceType.LIST_MEMBER) { return 1.0;}
				            if (t.getSentenceType() == SentenceType.LIST_START)  { return 1.0; }
				            if (t.getSentenceType() == SentenceType.UNKNOWN) { return 10.0; }
				            if (t.getSentenceType() == SentenceType.TITLE)   { return 50.0; }
				            break;
			case TITLE: if (t.getSentenceType() == SentenceType.LIST_MEMBER) { return 50.0;}
			            if (t.getSentenceType() == SentenceType.NORMAL)      { return 50.0; }
			            if (t.getSentenceType() == SentenceType.UNKNOWN) 	 { return 10.0; }
			            if (t.getSentenceType() == SentenceType.LIST_START)  { return 50.0; }
			            break;
			case LIST_START:  if (t.getSentenceType() == SentenceType.LIST_MEMBER) { return 0.0;}
                              if (t.getSentenceType() == SentenceType.NORMAL)  { return 1.0; }
                              if (t.getSentenceType() == SentenceType.UNKNOWN) { return 10.0; }
                              if (t.getSentenceType() == SentenceType.TITLE)   { return 50.0; }
                              break;
			case LIST_MEMBER:	if (t.getSentenceType() == SentenceType.LIST_START) { return 0.0;}
					            if (t.getSentenceType() == SentenceType.NORMAL)  { return 1.0; }
					            if (t.getSentenceType() == SentenceType.UNKNOWN) { return 10.0; }
					            if (t.getSentenceType() == SentenceType.TITLE)   { return 50.0; }
					            break;
		}
		
		return result;
	}
}
