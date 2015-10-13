package edu.ncsu.csc.nl.model.type;


/**
 * Represents the different ways we could use a word
 * - as it appears in the sentence (original), the lemma, or the porter stem
 * 
 * @author John
 *
 */
public enum WordType {
	
	ORIGINAL("original"),
	LEMMA("lemma"),
	STEM("stem");
	
	private String _label;
	
	private WordType(String label) {
		_label = label;
	}
	
	public String toString() {
		return _label;
	}
	

}
