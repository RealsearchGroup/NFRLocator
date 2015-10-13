package edu.ncsu.csc.nl.event;

import edu.ncsu.csc.nl.model.Sentence;

/**
 * Represents that some attribute of a sentence just changed
 * 
 * @author John Slankas
 */
public class NLPEventSentenceAccessControlMarkedEvent extends NLPEvent {

	private Sentence _sentence;
	
	public NLPEventSentenceAccessControlMarkedEvent(Sentence s) {
		_sentence = s;
	}
	
	public Sentence getSentence() {
		return _sentence;
	}
	
}
