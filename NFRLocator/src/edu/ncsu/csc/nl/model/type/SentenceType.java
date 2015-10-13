package edu.ncsu.csc.nl.model.type;


/**
 * Represents the different types of sentences that we could find
 * during the parsing.
 * 
 * @author John
 *
 */
public enum SentenceType {
	
	UNKNOWN("unknown","U"),
	NORMAL("normal", "N"),
	TITLE("title", "T"),
	LIST_START("list-start", "LS"),
	LIST_MEMBER("list-member", "LM");
	
	private String _label;
	private String _abbreviation;
	
	private SentenceType(String label, String abbr) {
		_label = label;
	}
	
	public String toString() {
		return _label;
	}
	
	public String getAbbreviation() { return _abbreviation; }

}
