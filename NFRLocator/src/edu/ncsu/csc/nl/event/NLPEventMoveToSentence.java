package edu.ncsu.csc.nl.event;

/**
 * Represents that a classification event just occurred for a particular sentence.
 * 
 * @author John Slankas
 */
public class NLPEventMoveToSentence extends NLPEvent  {

	private int _sentenceNumber;
	
	public NLPEventMoveToSentence(int moveToSentence) {
		_sentenceNumber = moveToSentence;
	}

	public int getSentenceNumber() {
		return _sentenceNumber;
	}
	
}
