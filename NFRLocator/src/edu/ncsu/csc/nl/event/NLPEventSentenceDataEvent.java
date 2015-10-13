package edu.ncsu.csc.nl.event;

import edu.ncsu.csc.nl.model.Sentence;

/**
 * Represents that some attribute of a sentence just changed
 * 
 * @author John Slankas
 */
public class NLPEventSentenceDataEvent extends NLPEvent {

	private Sentence _sentence;
	private String   _fieldName;
	
	public NLPEventSentenceDataEvent(Sentence s, String fieldName) {
		_sentence = s;
		_fieldName = fieldName;
	}
	
	public Sentence getSentence() {
		return _sentence;
	}
	
	public String getFieldName() {
		return _fieldName;
	}


	
	
}
