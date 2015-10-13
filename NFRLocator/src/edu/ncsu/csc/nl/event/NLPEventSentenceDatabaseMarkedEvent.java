package edu.ncsu.csc.nl.event;

import edu.ncsu.csc.nl.model.Sentence;

/**
 * Represents that a sentence had its database defined attribute changed
 * 
 * @author John Slankas
 */
public class NLPEventSentenceDatabaseMarkedEvent extends NLPEvent {

	private Sentence _sentence;
	
	public NLPEventSentenceDatabaseMarkedEvent(Sentence s) {
		_sentence = s;
	}
	
	public Sentence getSentence() {
		return _sentence;
	}
	
}
